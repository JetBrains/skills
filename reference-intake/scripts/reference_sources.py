"""Durably materialize ephemeral raster references for a Design I/O run."""

from __future__ import annotations

import hashlib
import json
import os
import re
import tempfile
from datetime import datetime, timezone
from pathlib import Path


MANIFEST_SCHEMA = "design-playbook.reference.manifest/v1"
EPHEMERAL_KINDS = frozenset({"screenshot", "other"})
SOURCE_KINDS = frozenset(
    {"screenshot", "url", "design_file", "product_analogy", "other"}
)
STORAGE_KINDS = frozenset({"copied", "linked", "remote", "symbolic"})
ACQUISITION_METHODS = frozenset(
    {"attachment", "local-file", "host-tool", "export", "url", "analogy"}
)
EPHEMERAL_ACQUISITION = frozenset(
    {"attachment", "local-file", "host-tool", "export"}
)


class ReferenceSourceError(ValueError):
    """Raised when a reference source cannot be safely materialized."""


def _validate_nonempty_text(value: object, label: str) -> str:
    if not isinstance(value, str) or not value.strip() or any(
        ord(character) < 32 or ord(character) == 127 for character in value
    ):
        raise ReferenceSourceError(
            f"{label} must be non-empty text without control characters"
        )
    return value


def _validate_identifier(value: object, label: str) -> str:
    identifier = _validate_nonempty_text(value, label)
    if "/" in identifier or "\\" in identifier:
        raise ReferenceSourceError(f"{label} must be safe non-empty text")
    return identifier


def _validate_provider(value: object, source_path: Path | None = None) -> str:
    provider = _validate_nonempty_text(value, "provider")
    path_like = "/" in provider or "\\" in provider
    if source_path is not None:
        source_names = {str(source_path).casefold(), source_path.name.casefold()}
        path_like = path_like or provider.casefold() in source_names
    if path_like:
        raise ReferenceSourceError("provider must be a provider label, not a path")
    return provider


def _validate_timestamp(value: object, label: str) -> None:
    timestamp = _validate_nonempty_text(value, label)
    normalized = timestamp[:-1] + "+00:00" if timestamp.endswith("Z") else timestamp
    try:
        datetime.fromisoformat(normalized)
    except ValueError as exc:
        raise ReferenceSourceError(f"{label} must be an ISO-8601 timestamp") from exc


def _ensure_within_run(path: Path, run_root: Path) -> None:
    resolved_root = run_root.resolve(strict=False)
    resolved_path = path.resolve(strict=False)
    try:
        resolved_path.relative_to(resolved_root)
    except ValueError as exc:
        raise ReferenceSourceError(
            f"destination escapes run root: {resolved_path}"
        ) from exc


def _empty_manifest(
    run_id: str, captured_at: str
) -> tuple[dict[str, object], list[dict[str, object]]]:
    """Fresh manifest skeleton for a run with no recorded sources yet."""
    sources: list[dict[str, object]] = []
    manifest: dict[str, object] = {
        "schema": MANIFEST_SCHEMA,
        "run_id": run_id,
        "captured_at": captured_at,
        "tool": "reference-intake",
        "sources": sources,
    }
    return manifest, sources


def _validate_source_entry(source: object, index: int, seen_ids: set[str]) -> None:
    """Validate one ``manifest.sources[index]`` entry, rejecting duplicates."""
    if not isinstance(source, dict):
        raise ReferenceSourceError(f"manifest sources[{index}] must be an object")
    source_id = _validate_identifier(
        source.get("id"), f"manifest sources[{index}].id"
    )
    if source_id in seen_ids:
        raise ReferenceSourceError(f"duplicate source id: {source_id!r}")
    seen_ids.add(source_id)
    if source.get("kind") not in SOURCE_KINDS:
        raise ReferenceSourceError(f"manifest sources[{index}].kind is invalid")
    _validate_nonempty_text(
        source.get("locator"), f"manifest sources[{index}].locator"
    )
    if "sha256" not in source:
        raise ReferenceSourceError(
            f"manifest sources[{index}].sha256 is required"
        )
    digest = source["sha256"]
    if digest is not None and (
        not isinstance(digest, str)
        or re.fullmatch(r"[0-9a-f]{64}", digest) is None
    ):
        raise ReferenceSourceError(
            f"manifest sources[{index}].sha256 must be null or lowercase hex"
        )
    storage = source.get("storage")
    if storage is not None and storage not in STORAGE_KINDS:
        raise ReferenceSourceError(f"manifest sources[{index}].storage is invalid")
    acquisition = source.get("acquired_via")
    if acquisition is not None and acquisition not in ACQUISITION_METHODS:
        raise ReferenceSourceError(
            f"manifest sources[{index}].acquired_via is invalid"
        )
    if "provider" in source:
        _validate_provider(source["provider"])
    if "media_type" in source:
        _validate_nonempty_text(
            source["media_type"], f"manifest sources[{index}].media_type"
        )
    if "captured_at" in source:
        _validate_timestamp(
            source["captured_at"], f"manifest sources[{index}].captured_at"
        )


def _load_manifest(
    manifest_path: Path, *, run_id: str, captured_at: str
) -> tuple[dict[str, object], list[dict[str, object]]]:
    if not manifest_path.exists():
        return _empty_manifest(run_id, captured_at)

    try:
        loaded = json.loads(manifest_path.read_text(encoding="utf-8"))
    except (OSError, UnicodeError, json.JSONDecodeError) as exc:
        raise ReferenceSourceError("manifest is not valid UTF-8 JSON") from exc
    if not isinstance(loaded, dict):
        raise ReferenceSourceError("manifest must be a JSON object")
    if loaded.get("schema") != MANIFEST_SCHEMA:
        raise ReferenceSourceError(f"manifest schema must be {MANIFEST_SCHEMA!r}")
    if loaded.get("run_id") != run_id:
        raise ReferenceSourceError("manifest run_id does not match the requested run_id")
    _validate_timestamp(loaded.get("captured_at"), "manifest captured_at")
    _validate_nonempty_text(loaded.get("tool"), "manifest tool")
    sources = loaded.get("sources")
    if not isinstance(sources, list):
        raise ReferenceSourceError("manifest sources must be a list")

    seen_ids: set[str] = set()
    for index, source in enumerate(sources):
        _validate_source_entry(source, index, seen_ids)
    return loaded, sources


def _atomic_write(path: Path, data: bytes) -> None:
    # ponytail: skill payload stays import-free of mcp.preview.transaction.
    path.parent.mkdir(parents=True, exist_ok=True)
    descriptor, temporary_name = tempfile.mkstemp(
        dir=path.parent, prefix=f".{path.name}.", suffix=".tmp"
    )
    temporary = Path(temporary_name)
    try:
        with os.fdopen(descriptor, "wb") as handle:
            handle.write(data)
            handle.flush()
            os.fsync(handle.fileno())
        os.replace(temporary, path)
    except OSError as exc:
        raise ReferenceSourceError(f"could not write {path.name}: {exc}") from exc
    finally:
        temporary.unlink(missing_ok=True)


def _safe_stem(path: Path) -> str:
    stem = re.sub(r"[^A-Za-z0-9._-]+", "-", path.stem).strip(".-")
    return stem or "image"


def _detect_image_type(data: bytes) -> tuple[str, str]:
    if data.startswith(b"\x89PNG\r\n\x1a\n"):
        return "image/png", ".png"
    if data.startswith(b"\xff\xd8\xff"):
        return "image/jpeg", ".jpg"
    if len(data) >= 12 and data.startswith(b"RIFF") and data[8:12] == b"WEBP":
        return "image/webp", ".webp"
    if data.startswith((b"GIF87a", b"GIF89a")):
        return "image/gif", ".gif"
    raise ReferenceSourceError("unsupported image signature")


def ingest_ephemeral_image(
    source_path: Path,
    run_root: Path,
    *,
    run_id: str,
    source_id: str,
    kind: str,
    acquired_via: str = "attachment",
    provider: str | None = None,
    captured_at: str | None = None,
) -> dict[str, object]:
    """Copy one ephemeral raster source and atomically update its manifest."""
    _validate_identifier(run_id, "run_id")
    _validate_identifier(source_id, "source_id")
    if kind not in EPHEMERAL_KINDS:
        raise ReferenceSourceError(f"kind must be one of {sorted(EPHEMERAL_KINDS)}")
    if acquired_via not in EPHEMERAL_ACQUISITION:
        raise ReferenceSourceError(
            "acquired_via for ephemeral ingest must be one of "
            f"{sorted(EPHEMERAL_ACQUISITION)}"
        )
    if provider is not None:
        _validate_provider(provider, source_path)
    timestamp = (
        datetime.now(timezone.utc).isoformat()
        if captured_at is None
        else captured_at
    )
    _validate_timestamp(timestamp, "captured_at")
    manifest_path = run_root / "reference" / "manifest.json"
    _ensure_within_run(manifest_path, run_root)
    manifest, sources = _load_manifest(
        manifest_path, run_id=run_id, captured_at=timestamp
    )
    if source_id in {source["id"] for source in sources}:
        raise ReferenceSourceError(f"duplicate source id: {source_id!r}")

    if not source_path.exists():
        raise ReferenceSourceError(f"source file does not exist: {source_path}")
    if not source_path.is_file():
        raise ReferenceSourceError(f"source path is not a regular file: {source_path}")
    try:
        source_bytes = source_path.read_bytes()
    except OSError as exc:
        raise ReferenceSourceError(f"source file cannot be read: {source_path}") from exc
    media_type, suffix = _detect_image_type(source_bytes)

    digest = hashlib.sha256(source_bytes).hexdigest()
    asset_name = f"{_safe_stem(source_path)}-{digest[:12]}{suffix}"
    locator = f"reference/assets/{asset_name}"
    asset_path = run_root / locator
    _ensure_within_run(asset_path, run_root)
    # Same digest may already be claimed by a prior manifest entry; only an
    # asset this call created may be rolled back on manifest failure.
    asset_existed = asset_path.exists()
    _atomic_write(asset_path, source_bytes)

    record: dict[str, object] = {
        "id": source_id,
        "kind": kind,
        "locator": locator,
        "sha256": digest,
        "media_type": media_type,
        "storage": "copied",
        "acquired_via": acquired_via,
        "captured_at": timestamp,
    }
    if provider is not None:
        record["provider"] = provider
    sources.append(record)
    payload = (json.dumps(manifest, indent=2, ensure_ascii=False) + "\n").encode("utf-8")
    try:
        _atomic_write(manifest_path, payload)
    except ReferenceSourceError:
        # Fail closed (issue #74): an asset no manifest entry points at is
        # an orphan. Remove this call's fresh asset before propagating; a
        # pre-existing asset stays — a prior manifest entry still claims it.
        if not asset_existed:
            try:
                asset_path.unlink(missing_ok=True)
            except OSError:
                pass  # cleanup best-effort; write failure still propagates
        raise
    return manifest

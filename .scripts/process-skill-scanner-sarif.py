#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import os
from collections import Counter
from copy import deepcopy
from pathlib import Path
from typing import Any
from urllib.parse import unquote, urlparse


LEVEL_ORDER = {"error": 0, "warning": 1, "note": 2, "none": 3}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Normalize and summarize skill-scanner SARIF output.")
    parser.add_argument("--input", required=True, help="Path to the input SARIF file")
    parser.add_argument("--summary", required=True, help="Path to the summary markdown output")
    parser.add_argument("--status", required=True, help="Path to the status JSON output")
    parser.add_argument("--blocking-sarif", required=True, help="Path to the filtered blocking SARIF output")
    return parser.parse_args()


def load_json(path: Path) -> Any:
    return json.loads(path.read_text(encoding="utf-8"))


def write_json(path: Path, payload: Any) -> None:
    path.write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")


def decode_uri(uri: str) -> str:
    if uri.startswith("file://"):
        parsed = urlparse(uri)
        path = unquote(parsed.path)
        if parsed.netloc:
            return f"//{parsed.netloc}{path}"
        return path
    return uri


def normalize_base_dir(uri: str, workspace: str) -> str | None:
    raw = decode_uri(uri)
    if not raw:
        return None
    if os.path.isabs(raw):
        normalized = os.path.normpath(raw)
        if workspace and normalized.startswith(workspace):
            return normalized
    return None


def pick_run_base(run: dict[str, Any], workspace: str) -> str | None:
    candidates: list[str] = []
    for base in (run.get("originalUriBaseIds") or {}).values():
        uri = base.get("uri")
        if isinstance(uri, str):
            candidates.append(uri)
    for invocation in run.get("invocations") or []:
        working_directory = (invocation.get("workingDirectory") or {}).get("uri")
        if isinstance(working_directory, str):
            candidates.append(working_directory)
    normalized = [path for path in (normalize_base_dir(uri, workspace) for uri in candidates) if path]
    if not normalized:
        return None
    normalized.sort(key=len, reverse=True)
    return normalized[0]


def normalize_uri(uri: str | None, run_base: str | None, workspace: str) -> str:
    if not uri:
        return "(no location)"
    if uri == "(cross-skill analysis)":
        return "cross-skill-analysis"

    decoded = decode_uri(uri)
    archive_root, archive_sep, archive_inner = decoded.partition("!/")

    if os.path.isabs(archive_root):
        absolute_root = os.path.normpath(archive_root)
    elif run_base:
        absolute_root = os.path.normpath(os.path.join(run_base, archive_root))
    else:
        absolute_root = archive_root

    if workspace and os.path.isabs(absolute_root) and absolute_root.startswith(workspace):
        relative_root = os.path.relpath(absolute_root, workspace).replace(os.sep, "/")
    else:
        relative_root = absolute_root.replace(os.sep, "/")

    if archive_sep:
        return f"{relative_root}!/{archive_inner}"
    return relative_root


def first_location(result: dict[str, Any]) -> tuple[str, int | None]:
    locations = result.get("locations") or []
    if not locations:
        return "(no location)", None
    physical = locations[0].get("physicalLocation") or {}
    artifact = physical.get("artifactLocation") or {}
    uri = artifact.get("uri") or "(no location)"
    region = physical.get("region") or {}
    return str(uri), region.get("startLine")


def message_for(result: dict[str, Any]) -> str:
    message = result.get("message") or {}
    text = message.get("text") or message.get("markdown") or "(no message)"
    return " ".join(str(text).split())


def result_sort_key(record: dict[str, Any]) -> tuple[int, str, str, int]:
    return (
        LEVEL_ORDER.get(record["level"], 99),
        record["rule_id"],
        record["uri"],
        record["line"] or 0,
    )


def write_summary(
    path: Path,
    all_records: list[dict[str, Any]],
    blocking_records: list[dict[str, Any]],
) -> None:
    lines: list[str] = []
    all_levels = Counter(record["level"] for record in all_records)
    blocking_levels = Counter(record["level"] for record in blocking_records)
    blocking_error_records = [record for record in blocking_records if record["level"] == "error"]
    blocking_rules = Counter(record["rule_id"] for record in blocking_records)

    lines.append("## Skill Scanner Summary")
    lines.append("")
    lines.append(f"- Total results: {len(all_records)}")
    lines.append(f"- Blocking results: {len(blocking_records)}")
    lines.append(f"- Blocking error results: {len(blocking_error_records)}")
    for level in ("error", "warning", "note", "none"):
        if all_levels.get(level):
            lines.append(f"- total {level}: {all_levels[level]}")
    for level in ("error", "warning", "note", "none"):
        if blocking_levels.get(level):
            lines.append(f"- blocking {level}: {blocking_levels[level]}")

    if blocking_rules:
        lines.extend(["", "### Top Blocking Rule IDs", "", "| Rule | Count |", "| --- | ---: |"])
        for rule_id, count in blocking_rules.most_common(10):
            lines.append(f"| `{rule_id}` | {count} |")

    if blocking_records:
        lines.extend(["", f"### Blocking Findings ({min(20, len(blocking_records))})", ""])
        for index, record in enumerate(sorted(blocking_records, key=result_sort_key)[:20], start=1):
            location = f"{record['uri']}:{record['line']}" if record["line"] else record["uri"]
            lines.append(f"{index}. [{record['level']}] `{record['rule_id']}` at `{location}`")
            lines.append(f"   {record['message']}")
    else:
        lines.extend(["", "### Blocking Findings", "", "No blocking findings remain."])

    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> int:
    args = parse_args()
    input_path = Path(args.input)
    summary_path = Path(args.summary)
    status_path = Path(args.status)
    blocking_sarif_path = Path(args.blocking_sarif)

    data = load_json(input_path)
    workspace = os.path.normpath(os.environ.get("GITHUB_WORKSPACE") or str(input_path.resolve().parents[2]))

    all_records: list[dict[str, Any]] = []
    blocking_data = deepcopy(data)

    for run, blocking_run in zip(data.get("runs", []), blocking_data.get("runs", [])):
        run_base = pick_run_base(run, workspace)
        blocking_results: list[dict[str, Any]] = []
        for result in run.get("results", []):
            for location in result.get("locations") or []:
                physical = location.get("physicalLocation") or {}
                artifact = physical.get("artifactLocation") or {}
                artifact["uri"] = normalize_uri(artifact.get("uri"), run_base, workspace)

            uri, line = first_location(result)
            level = str(result.get("level", "warning"))
            rule_id = str(result.get("ruleId", "(unknown-rule)"))
            message = message_for(result)
            record = {
                "level": level,
                "rule_id": rule_id,
                "uri": uri,
                "line": line,
                "message": message,
            }
            all_records.append(record)
            blocking_results.append(result)
        blocking_run["results"] = blocking_results

    blocking_records = list(all_records)

    write_json(input_path, data)
    write_json(blocking_sarif_path, blocking_data)
    write_summary(summary_path, all_records, blocking_records)
    write_json(
        status_path,
        {
            "total_count": len(all_records),
            "blocking_count": len(blocking_records),
            "blocking_error_count": len([record for record in blocking_records if record["level"] == "error"]),
            "blocking_levels": Counter(record["level"] for record in blocking_records),
        },
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

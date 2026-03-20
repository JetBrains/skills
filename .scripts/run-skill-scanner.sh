#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd)"
report_dir="${SKILL_SCANNER_REPORT_DIR:-$repo_root/.reports/skill-scanner}"
cache_root="${XDG_CACHE_HOME:-$HOME/.cache}/skill-scanner"
venv_dir="${SKILL_SCANNER_VENV:-$cache_root/skill-scanner-venv}"
requested_python="${SKILL_SCANNER_PYTHON:-}"
docker_image="${SKILL_SCANNER_DOCKER_IMAGE:-python:3.11-slim}"

run_with_docker() {
  if ! command -v docker >/dev/null 2>&1; then
    echo "Python 3.10+ is required to run skill-scanner, and Docker is not installed." >&2
    exit 1
  fi

  if ! docker info >/dev/null 2>&1; then
    echo "Docker is installed, but the Docker daemon is not running." >&2
    echo "Start Docker or provide Python 3.10+ via SKILL_SCANNER_PYTHON." >&2
    exit 1
  fi

  mkdir -p "$report_dir" "$cache_root/pip"

  if [[ "$#" -eq 0 ]]; then
    set -- --format markdown --detailed --output "/workspace/.reports/skill-scanner/skill-scan-report.md"
  fi

  echo "Running skill-scanner against: $repo_root"
  echo "Using Docker image: $docker_image"

  docker run --rm \
    -v "$repo_root:/workspace" \
    -v "$cache_root/pip:/root/.cache/pip" \
    -w /workspace \
    "$docker_image" \
    /bin/sh -lc '
      set -eu
      python -m pip install --upgrade pip >/dev/null
      python -m pip install --upgrade cisco-ai-skill-scanner >/dev/null
      skill-scanner scan-all \
        /workspace \
        --recursive \
        --lenient \
        --check-overlap \
        --use-behavioral \
        "$@"
    ' sh "$@"
}

find_python() {
  if [[ -n "$requested_python" ]]; then
    if command -v "$requested_python" >/dev/null 2>&1; then
      command -v "$requested_python"
      return 0
    fi
    echo "Configured SKILL_SCANNER_PYTHON was not found: $requested_python" >&2
    exit 1
  fi

  local candidate
  for candidate in python3.13 python3.12 python3.11 python3.10 python3; do
    if command -v "$candidate" >/dev/null 2>&1; then
      command -v "$candidate"
      return 0
    fi
  done

  echo "Python 3.10+ is required to run skill-scanner." >&2
  exit 1
}

python_bin="$(find_python)"

if ! "$python_bin" - <<'PY'
import sys
raise SystemExit(0 if sys.version_info >= (3, 10) else 1)
PY
then
  echo "Local Python is too old for skill-scanner: $("$python_bin" --version 2>&1)" >&2
  echo "Falling back to Docker." >&2
  run_with_docker "$@"
  exit 0
fi

mkdir -p "$cache_root" "$report_dir"

if [[ ! -x "$venv_dir/bin/python" ]]; then
  "$python_bin" -m venv "$venv_dir"
fi

"$venv_dir/bin/python" -m pip install --upgrade pip >/dev/null
"$venv_dir/bin/python" -m pip install --upgrade cisco-ai-skill-scanner >/dev/null

default_report="$report_dir/skill-scan-report.md"
if [[ "$#" -eq 0 ]]; then
  set -- --format markdown --detailed --output "$default_report"
fi

echo "Running skill-scanner against: $repo_root"
echo "Using Python: $("$venv_dir/bin/python" --version 2>&1)"

"$venv_dir/bin/skill-scanner" scan-all \
  "$repo_root" \
  --recursive \
  --lenient \
  --check-overlap \
  --use-behavioral \
  "$@"

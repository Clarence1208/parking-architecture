#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COVERAGE=false

usage() {
  echo "Usage: $0 [--coverage]"
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --coverage)
      COVERAGE=true
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      usage
      exit 1
      ;;
  esac
done

run_backend_tests() {
  echo "==> Running backend tests..."
  if [[ "$COVERAGE" == "true" ]]; then
    (cd "$ROOT_DIR/backend" && ./gradlew clean test jacocoTestReport)
    echo "Backend coverage report: $ROOT_DIR/backend/build/reports/jacoco/test/html/index.html"
  else
    (cd "$ROOT_DIR/backend" && ./gradlew clean test)
  fi
}

run_frontend_tests() {
  echo "==> Running frontend tests..."
  (cd "$ROOT_DIR/frontend" && npm ci)
  if [[ "$COVERAGE" == "true" ]]; then
    (cd "$ROOT_DIR/frontend" && npm run test:coverage)
    echo "Frontend coverage report: $ROOT_DIR/frontend/coverage/index.html"
  else
    (cd "$ROOT_DIR/frontend" && npm run test)
  fi
}

run_backend_tests
run_frontend_tests

echo "==> All tests completed successfully."

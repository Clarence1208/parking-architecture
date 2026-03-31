#!/bin/bash

MODE="prod"

for arg in "$@"; do
  case $arg in
    --dev)  MODE="dev"  ;;
    --prod) MODE="prod" ;;
    *)
      echo "Unknown argument: $arg"
      echo "Usage: $0 [--dev|--prod]"
      exit 1
      ;;
  esac
done

if [ "$MODE" = "dev" ]; then
  echo "Starting DEV environment (watch mode)..."
  docker compose -f docker-compose.dev.yml up --build
else
  echo "Starting PROD environment..."
  docker compose -f docker-compose.yml up --build
fi
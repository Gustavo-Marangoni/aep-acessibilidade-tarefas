#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if ! command -v mvn >/dev/null 2>&1; then
  echo "[ERRO] Apache Maven nao encontrado."
  exit 1
fi
if [ ! -f db.properties ]; then
  echo "[ERRO] Copie db.properties.example para db.properties e configure a senha."
  exit 1
fi
if [ "${1:-}" = "--voz" ]; then
  mvn -q compile exec:java -Dexec.args="--voz"
else
  mvn -q compile exec:java
fi

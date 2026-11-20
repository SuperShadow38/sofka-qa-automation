#!/usr/bin/env bash
set -uo pipefail
cd "$(dirname "$0")/.." || exit 1
suite="${1:-all}"
case "$suite" in web|api|all) ;; *) echo 'Uso: bash scripts/run.sh web|api|all [opciones Maven]' >&2; exit 2;; esac
if [ "$#" -gt 0 ]; then shift; fi
destination="evidence/runs/$(date -u +%Y%m%d-%H%M%S)-$suite-bash"
mkdir -p "$destination" || exit 2
touch "$destination/started" || exit 2
args=(-B -ntp -fae)
if [ "$suite" != all ]; then args+=(-pl "$suite-tests"); fi
args+=(clean verify "$@")
bash ./mvnw "${args[@]}" 2>&1 | tee "$destination/console.log"
statuses=("${PIPESTATUS[@]}")
result=${statuses[0]}
if [ "$result" -eq 0 ] && [ "${statuses[1]}" -ne 0 ]; then result=2; fi
archive_status=0
modules=($suite)
if [ "$suite" = all ]; then modules=(web api); fi
for module in "${modules[@]}"; do
  target="$module-tests/target"
  for folder in failsafe-reports surefire-reports site/serenity karate-reports cleanup-verification; do
    report="$target/$folder"
    if [ -d "$report" ] && [ -n "$(find "$report" -type f -newer "$destination/started" -print -quit)" ]; then
      mkdir -p "$destination/$module" || archive_status=2
      cp -R "$report" "$destination/$module/" || archive_status=2
    fi
  done
done
if [ "$result" -eq 0 ] && [ "$archive_status" -ne 0 ]; then result=2; fi
if ! printf '%s\n' "Command: bash mvnw ${args[*]}" "Exit code: $result" > "$destination/execution.txt"; then
  if [ "$result" -eq 0 ]; then result=2; fi
fi
echo "Evidencias: $destination"
exit "$result"

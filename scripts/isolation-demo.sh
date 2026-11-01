#!/usr/bin/env bash
set -euo pipefail

# Git Bash/Linux/macOS: bash scripts/isolation-demo.sh
BASE="${BASE:-http://localhost:8080/test-demo/api}"
CONTAINER="${CONTAINER:-school-postgres}"
DATABASE="${DATABASE:-school_task21_db}"
DELAY_MS=5000
OUT="${OUT:-build/isolation-demo/$(date +%Y%m%d-%H%M%S)-$$}"
for dependency in curl node docker; do
    command -v "$dependency" >/dev/null || { echo "Missing command: $dependency" >&2; exit 1; }
done
mkdir -p "$OUT"
department_id=''
employee_id=''
student_ids=()
pending=()

sql() { docker exec "$CONTAINER" psql -U postgres -d "$DATABASE" -At -v ON_ERROR_STOP=1 -c "$1" | tr -d '\r'; }
field() { node -e 'const fs=require("fs"); console.log(JSON.parse(fs.readFileSync(process.argv[1],"utf8"))[process.argv[2]]);' "$1" "$2"; }
expect() { [[ "$1" == "$2" ]] || { echo "FAIL: $3; expected=$2 actual=$1" >&2; return 1; }; }

http() {
    local method="$1" path="$2" name="$3" body="${4:-}" status
    local args=(-sS --max-time 35 -X "$method" -o "$OUT/$name.json" -w '%{http_code}')
    if [[ -n "$body" ]]; then args+=(-H 'Content-Type: application/json' --data "$body"); fi
    status=$(curl "${args[@]}" "$BASE/$path")
    printf '%s' "$status" > "$OUT/$name.status"
}
checked_http() {
    http "$1" "$2" "$3" "${5:-}"
    expect "$(cat "$OUT/$3.status")" "$4" "$1 $2 (see $OUT/$3.json)"
}
cleanup() {
    local exit_code=$?
    trap - EXIT
    set +e
    for pid in "${pending[@]}"; do wait "$pid"; done
    local cleanup_failed=0 id
    for id in "${student_ids[@]}"; do
        checked_http DELETE "students/$id" "cleanup-student-$id" 204 || cleanup_failed=1
    done
    if [[ -n "$employee_id" ]]; then checked_http DELETE "employees/$employee_id" cleanup-employee 204 || cleanup_failed=1; fi
    if [[ -n "$department_id" ]]; then checked_http DELETE "departments/$department_id" cleanup-department 204 || cleanup_failed=1; fi
    if (( cleanup_failed )); then echo 'Cleanup failed; inspect cleanup responses.' >&2; exit_code=1; fi
    echo "Evidence: $OUT"
    exit "$exit_code"
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

wait_for_reads() {
    local required="$1" predicate="$2" active attempt
    for ((attempt=0;attempt<25;attempt++)); do
        active=$(sql "SELECT count(*) FROM pg_stat_activity WHERE datname=current_database() AND state='idle in transaction' AND ($predicate);")
        if (( active >= required )); then return; fi
        sleep 0.1
    done
    echo 'FAIL: simultaneous reads were not observed before writes; result is inconclusive.' >&2
    exit 1
}
wait_requests() {
    local pid
    for pid in "${pending[@]}"; do wait "$pid"; done
    pending=()
}
pair_status() { printf '%s\n%s\n' "$(cat "$OUT/$1.status")" "$(cat "$OUT/$2.status")" | sort | paste -sd, -; }
row() { printf '%-3s %-17s %-9s %s\n' "$1" "$2" "$3" "$4" | tee -a "$OUT/summary.txt"; }

expect "$(sql 'SELECT capacity FROM courses WHERE id=1;')" 1 'Seed course1 capacity'
expect "$(sql 'SELECT count(*) FROM student_course WHERE course_id=1;')" 0 'Course1 must be empty; do not run alongside other enrollment tests'
token="$(date +%s)-$$"
checked_http POST departments setup-department 201 "{\"name\":\"Isolation-$token\",\"location\":\"Demo\",\"phone\":\"123\"}"
department_id=$(field "$OUT/setup-department.json" id)
employee_body() { printf '{"name":"Isolation","surname":"%s","salary":%s,"departmentId":%s}' "$token" "$1" "$department_id"; }
checked_http POST employees setup-employee 201 "$(employee_body 1000)"
employee_id=$(field "$OUT/setup-employee.json" id)
printf '%-3s %-17s %-9s %s\n' 'Demo' 'Isolation' 'HTTP' 'Observed result' | tee "$OUT/summary.txt"

# D1: writer commits after the first AVG, before the second AVG.
for level in READ_COMMITTED REPEATABLE_READ; do
    checked_http PUT "employees/$employee_id" "d1-$level-reset" 200 "$(employee_body 1000)"
    http GET "isolation/departments/$department_id/salary-report?isolation=$level&delayMs=$DELAY_MS" "d1-$level" &
    pending=("$!")
    wait_for_reads 1 "query ILIKE '%avg(%' AND query ILIKE '%employees%'"
    checked_http PUT "employees/$employee_id" "d1-$level-writer" 200 "$(employee_body 3000)"
    [[ ! -f "$OUT/d1-$level.status" ]] || { echo 'FAIL: report ended before writer; overlap not proven.' >&2; exit 1; }
    wait_requests
    expect "$(cat "$OUT/d1-$level.status")" 200 D1
    expect "$(field "$OUT/d1-$level.json" firstAvg)" 1000 'D1 first average'
    expected=3000; [[ "$level" != REPEATABLE_READ ]] || expected=1000
    expect "$(field "$OUT/d1-$level.json" secondAvg)" "$expected" 'D1 second average'
    row D1 "$level" 200 "AVG 1000 -> $expected"
done

# D2: both requests read salary1000 before either writes salary1100.
for level in READ_COMMITTED REPEATABLE_READ; do
    checked_http PUT "employees/$employee_id" "d2-$level-reset" 200 "$(employee_body 1000)"
    http POST "isolation/employees/$employee_id/raise?amount=100&isolation=$level&delayMs=$DELAY_MS" "d2-$level-a" &
    pending=("$!")
    http POST "isolation/employees/$employee_id/raise?amount=100&isolation=$level&delayMs=$DELAY_MS" "d2-$level-b" &
    pending+=("$!")
    wait_for_reads 2 "query ILIKE '%employees%' AND query NOT ILIKE '%avg(%'"
    wait_requests
    statuses=$(pair_status "d2-$level-a" "d2-$level-b")
    expected=200,200; [[ "$level" != REPEATABLE_READ ]] || expected=200,409
    expect "$statuses" "$expected" 'D2 statuses'
    expect "$(sql "SELECT salary::numeric::text FROM employees WHERE id=$employee_id;")" 1100.00 'D2 final salary'
    row D2 "$level" "$statuses" 'salary=1100; two successful increments would give1200'
done

# D3: different join rows, no UPDATE of the shared course row.
for level in REPEATABLE_READ SERIALIZABLE; do
    for index in 1 2; do
        checked_http POST students "d3-$level-student$index" 201 "{\"name\":\"Isolation$index\",\"surname\":\"$token\",\"age\":22}"
        student_ids+=("$(field "$OUT/d3-$level-student$index.json" id)")
    done
    http POST "isolation/courses/1/enroll/${student_ids[0]}?isolation=$level&delayMs=$DELAY_MS" "d3-$level-a" &
    pending=("$!")
    http POST "isolation/courses/1/enroll/${student_ids[1]}?isolation=$level&delayMs=$DELAY_MS" "d3-$level-b" &
    pending+=("$!")
    wait_for_reads 2 "query ILIKE '%student_course%' AND query ILIKE '%count(%'"
    wait_requests
    statuses=$(pair_status "d3-$level-a" "d3-$level-b")
    expected=200,200; count=2
    if [[ "$level" == SERIALIZABLE ]]; then expected=200,409; count=1; fi
    expect "$statuses" "$expected" 'D3 statuses'
    expect "$(sql 'SELECT count(*) FROM student_course WHERE course_id=1;')" "$count" 'D3 final enrollments'
    for side in a b; do
        if [[ "$(cat "$OUT/d3-$level-$side.status")" == 200 ]]; then
            expect "$(field "$OUT/d3-$level-$side.json" countBefore)" 0 'D3 initial count'
        fi
    done
    row D3 "$level" "$statuses" "capacity=1; enrolled=$count"
    for id in "${student_ids[@]}"; do checked_http DELETE "students/$id" "d3-cleanup-$id" 204; done
    student_ids=()
done
echo 'PASS: all six comparisons matched; temporary data will now be removed.' | tee -a "$OUT/summary.txt"

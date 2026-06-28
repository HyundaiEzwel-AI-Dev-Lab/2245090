# DB 스키마

초기 스키마는 Flyway migration으로 관리한다.

- `backend/src/main/resources/db/migration/V1__create_pms_schema.sql`
- `backend/src/main/resources/db/migration/V2__seed_pms_data.sql`

## 주요 테이블

| 테이블 | 설명 |
|---|---|
| `pms_users` | 사용자 |
| `projects` | 프로젝트 |
| `requirements` | 요구사항 |
| `wbs_tasks` | WBS 작업단위 |
| `test_cases` | 테스트 케이스 |
| `defects` | 결함 |
| `change_histories` | 변경이력 |

## 관계

```text
projects 1 ─ N requirements
projects 1 ─ N wbs_tasks
requirements 1 ─ N wbs_tasks
wbs_tasks 1 ─ N test_cases
projects 1 ─ N defects
test_cases 1 ─ N defects
projects 1 ─ N change_histories
```

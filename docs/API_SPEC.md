# API 명세 초안

공통 응답:

```json
{
  "success": true,
  "data": {},
  "message": "OK"
}
```

## Dashboard

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/dashboard/summary` | 대시보드 요약 |

## Project

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/projects` | 프로젝트 검색 |
| POST | `/api/projects` | 프로젝트 등록 |
| GET | `/api/projects/{projectId}` | 프로젝트 상세 |

검색 파라미터:

- `keyword`
- `status`
- `openMonth` 형식: `YYYY-MM`

## Requirement

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/projects/{projectId}/requirements` | 요구사항 목록 |
| POST | `/api/projects/{projectId}/requirements` | 요구사항 등록 |
| PATCH | `/api/requirements/{requirementId}/confirm` | 요구사항 확정 및 WBS 생성 |

## WBS

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/projects/{projectId}/wbs-tasks` | 프로젝트 WBS 목록 |
| GET | `/api/my-work/wbs-tasks?assignee=김현대` | 내업무 WBS 목록 |
| PATCH | `/api/wbs-tasks/{taskId}/schedule` | 일정 변경 |
| PATCH | `/api/wbs-tasks/{taskId}/start` | 착수 처리 |
| PATCH | `/api/wbs-tasks/{taskId}/complete` | 완료 처리 |

## Test / Defect

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/projects/{projectId}/test-cases` | 테스트 케이스 목록 |
| POST | `/api/projects/{projectId}/test-cases` | 테스트 케이스 등록 |
| PATCH | `/api/test-cases/{testCaseId}/execute` | 테스트 수행 |
| GET | `/api/projects/{projectId}/defects` | 결함 목록 |

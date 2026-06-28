# 개발 작업 분배안

4명 기준 협업 분리안이다.

| 담당 | 영역 | 주요 파일 |
|---|---|---|
| A | 공통/레이아웃/대시보드 | frontend/layouts, components/common, backend/dashboard |
| B | 프로젝트/요구사항 | frontend/pages/projects, backend/project, backend/requirement |
| C | WBS/내업무 | frontend/pages/my-work, components/wbs, backend/wbs |
| D | 테스트/결함/시스템관리 | backend/test, backend/defect, frontend test/defect 영역 |

## 충돌 방지 규칙

- 공통 컴포넌트 수정 전 팀 공유
- API DTO 변경 시 `API_SPEC.md` 먼저 수정
- DB 변경 시 Flyway migration 신규 파일 추가
- 같은 페이지를 여러 명이 동시에 수정하지 않는다.

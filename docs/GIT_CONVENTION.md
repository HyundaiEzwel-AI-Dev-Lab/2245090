# Git / Branch Convention

## Branch

```text
main
develop
feature/project-list
feature/requirement-confirm
feature/wbs-schedule
feature/test-defect
fix/api-error-handler
```

## Commit

```text
feat(project): 프로젝트 검색 기능 추가
feat(requirement): 요구사항 확정 시 WBS 생성
fix(wbs): 일정 시작일 종료일 검증 보완
refactor(frontend): 공통 카드 컴포넌트 분리
docs(api): WBS API 명세 추가
```

## PR 체크리스트

- [ ] 로컬 실행 확인
- [ ] API 응답 형식 확인
- [ ] DTO/타입 변경 시 문서 수정
- [ ] DB 변경 시 migration 추가
- [ ] 불필요한 console/log 제거

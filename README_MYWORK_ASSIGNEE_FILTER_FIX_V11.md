# PMS MyWork Assignee Filter Fix v11

내업무 화면에 다른 사람 업무/프로젝트가 보이는 문제를 수정합니다.

## 수정 전 문제

- `myTasks`가 비어 있으면 프로젝트 전체 WBS(`wbsTasks`)를 내업무에 fallback으로 보여줌
- 그 결과 내업무 화면에 다른 담당자의 업무가 노출될 수 있음
- 진행 프로젝트/대기 프로젝트도 전체 프로젝트 기준으로 보일 수 있음

## 수정 후

- 내업무 화면은 반드시 `task.assignee === 현재 선택 사용자` 조건으로 필터링
- `myTasks + wbsTasks`를 합치더라도 담당자가 다른 업무는 제거
- 진행 프로젝트는 내가 담당자인 WBS가 있는 프로젝트만 표시
- 대기 프로젝트도 내가 담당자인 WBS가 있는 접수 프로젝트만 표시
- WBS 관리 화면은 프로젝트 전체 WBS를 유지

## 덮어쓸 파일

```text
frontend/pages/index.vue
```

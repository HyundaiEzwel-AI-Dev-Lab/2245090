# PMS Project Progress Sync Fix v7

프로젝트 카드의 공정률과 WBS/내 할 일 업무 공정률이 서로 안 맞는 문제를 수정합니다.

## 수정 전

프로젝트 카드:
- `projects.progress_rate` 컬럼 값을 그대로 표시

내 할 일:
- `wbs_tasks.progress_rate` 값을 표시

그래서 WBS 업무 2개가 둘 다 100%인데 프로젝트 카드는 35%처럼 다르게 보일 수 있었습니다.

## 수정 후

프로젝트 카드/프로젝트 현황/프로젝트 정보의 공정률은 해당 프로젝트의 WBS 업무 공정률 평균으로 계산합니다.

```text
프로젝트 공정률 = 해당 프로젝트 WBS progressRate 평균
```

WBS가 없는 프로젝트는 기존 `projects.progressRate` 값을 사용합니다.

## 덮어쓸 파일

```text
frontend/pages/index.vue
```

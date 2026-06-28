# PMS SB Reading + Code v10

SB를 다시 기준으로 잡은 내업무/캘린더/일정관리 코드 패치입니다.

## 반영 기준

- IA/운영 Process: 내업무는 배정된 프로젝트 및 WBS 모아보기 & 일정 관리
- 카드형: KPI 5종, 진행중 프로젝트, 내 할 일, 대기 영역
- 내 할 일: WBS 업무명, 계획종료일 D-day, 공정률, 프로젝트명, 지연 강조, 업무유형별 랜딩
- 캘린더형: 일정 등록된 업무만 캘린더 노출, 일정 미등록 업무 별도 노출, 일정 등록 버튼
- 캘린더: 기간형 바 형태, 완료/지연/일시중단 상태 표시, +N 더보기
- 일정관리 팝업: WBS 정보, 최초 계획일 등록, 착수/완료, 일정변경 요청, 변경사유/결재선/승인요청, 추가정보

## 덮어쓸 파일

```text
frontend/pages/index.vue
frontend/assets/css/original-sb.css
```

## 같이 필요하면 유지할 파일

기존 v6에서 추가한 아래 파일들이 없다면 같이 있어야 합니다.

```text
frontend/nuxt.config.ts
frontend/server/api/[...path].ts
frontend/types/pms.ts
frontend/composables/useApi.ts
frontend/services/pmsService.ts
```

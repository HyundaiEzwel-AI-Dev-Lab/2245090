# PMS Vue SB Visible Data Fix v5

조회 완료는 뜨는데 내업무/캘린더가 비어 보이는 문제를 수습하는 패치입니다.

## 수정 내용

- 기본 담당자를 `이지윤`에서 백엔드 seed/log에 맞춘 `김현대`로 변경
- 상단 담당자 선택 콤보 추가
- `/api/projects`가 성공해도 실제 프로젝트/WBS가 비어 있으면 화면이 비지 않도록 시연용 fallback 데이터 표시
- 캘린더 기준 월을 업무 일정이 있는 2026년 7월로 보정
- API 실패가 아니라 데이터 없음인 경우에도 카드/캘린더가 보이도록 처리

## 덮어쓸 파일

```text
frontend/pages/index.vue
frontend/assets/css/sb.css
```

## 실행

```powershell
cd D:\pms_fullstack_clean_sb\frontend
Ctrl + C
npm run dev
```

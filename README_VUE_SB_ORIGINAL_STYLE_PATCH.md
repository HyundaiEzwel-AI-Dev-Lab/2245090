# PMS Vue SB Original Style Patch v4

기존에 잘 만들어둔 `index.html` UI 톤을 버리지 않고 Vue/Nuxt로 다시 구성한 패치입니다.

## 핵심

- iframe 방식 제거
- HTML 정적 JS 방식 제거
- Vue/Nuxt 페이지로 구성
- 기존 HTML의 LNB / Topbar / Tab / Card / Calendar / Modal 톤 유지
- Spring Boot API 연결 유지
- 일정관리 저장 / 착수 / 완료 API 연결
- 일정관리 모달 UI 깨짐 방지
- 내업무 캘린더를 진짜 월간 캘린더로 구현

## 덮어쓸/추가할 파일

```text
frontend/nuxt.config.ts
frontend/pages/index.vue
frontend/assets/css/sb.css
frontend/types/pms.ts
frontend/composables/useApi.ts
frontend/services/projectService.ts
frontend/services/requirementService.ts
frontend/services/wbsService.ts
frontend/services/testService.ts
```

## 실행

```powershell
cd D:\pms_fullstack_clean_sb
docker compose up -d postgres

cd D:\pms_fullstack_clean_sb\backend
.\gradlew.bat bootRun

cd D:\pms_fullstack_clean_sb\frontend
npm run dev
```

브라우저:

```text
http://localhost:3000
```

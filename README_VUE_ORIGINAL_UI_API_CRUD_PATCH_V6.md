# PMS Vue Original UI API CRUD Patch v6

이번 패치는 사용자가 올린 `index.html`의 UI 톤과 메뉴 구조를 기준으로 Vue/Nuxt로 다시 작성한 버전입니다.

## 핵심 원칙

- iframe 사용 안 함
- public HTML 사용 안 함
- 원본 HTML CSS 톤을 `assets/css/original-sb.css`로 유지
- `pages/index.vue`에서 Vue 상태 기반으로 메뉴/탭/모달/캘린더 동작
- API는 Nuxt server route `/api/**`가 Spring Boot `http://localhost:8080/api/**`로 프록시
- 프로젝트 등록 / 요구사항 등록 / 요구사항 확정 / WBS 일정 저장 / 착수 / 완료 / 테스트 결과 저장 연결

## 덮어쓸 파일

```text
frontend/nuxt.config.ts
frontend/server/api/[...path].ts
frontend/pages/index.vue
frontend/assets/css/original-sb.css
frontend/types/pms.ts
frontend/composables/useApi.ts
frontend/services/pmsService.ts
```

## 실행

```powershell
cd D:\pms_fullstack_clean_sb\frontend
npm run dev
```

백엔드/DB는 켜져 있어야 합니다.

```powershell
cd D:\pms_fullstack_clean_sb
docker compose up -d postgres

cd D:\pms_fullstack_clean_sb\backend
.\gradlew.bat bootRun
```

# 원본 HTML 기반 PMS 패치

사용자가 만든 `index.html` UI를 최대한 그대로 살리고, Nuxt에서는 해당 HTML을 iframe으로 띄웁니다.

## 적용 파일

```text
frontend/nuxt.config.ts
frontend/pages/index.vue
frontend/public/sb-poc.html
```

## 실행 전 확인

```powershell
cd D:\pms_fullstack_clean_sb
docker compose up -d postgres

cd D:\pms_fullstack_clean_sb\backend
.\gradlew.bat bootRun
```

백엔드 확인:

```text
http://localhost:8080/api/projects
```

## 프론트 실행

```powershell
cd D:\pms_fullstack_clean_sb\frontend
npm run dev
```

브라우저:

```text
http://localhost:3000
```

## 연결 방식

Nuxt devProxy가 `/api` 요청을 `http://localhost:8080/api`로 넘깁니다.

원본 HTML의 mock 배열은 유지하되, 로딩 시 아래 API가 성공하면 실제 데이터로 교체합니다.

```text
GET /api/projects
GET /api/projects/{projectId}/requirements
GET /api/projects/{projectId}/wbs-tasks
GET /api/projects/{projectId}/test-cases
GET /api/projects/{projectId}/defects
GET /api/my-work/wbs-tasks?assignee=이지윤
```

API가 실패하면 기존 HTML mock 데이터로 화면을 표시합니다.

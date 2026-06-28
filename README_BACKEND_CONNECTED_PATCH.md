# PMS Frontend Backend Connected Patch

이 패치는 프론트 UI를 SB 스타일로 유지하면서 Spring Boot Backend API와 다시 연결한 버전입니다.

## 덮어쓸/추가할 파일

```text
frontend/nuxt.config.ts
frontend/app.vue
frontend/pages/index.vue
frontend/pages/my-work.vue
frontend/assets/css/main.css
frontend/composables/useApi.ts
frontend/services/projectService.ts
frontend/services/requirementService.ts
frontend/services/wbsService.ts
frontend/services/testService.ts
frontend/types/pms.ts
frontend/components/api/WbsTable.vue
frontend/components/api/ProjectTable.vue
```

## 먼저 켜져 있어야 하는 것

```powershell
# Terminal 1
cd D:\pms_fullstack_clean_sb
docker compose up -d postgres

# Terminal 2
cd D:\pms_fullstack_clean_sb\backend
.\gradlew.bat bootRun
```

Backend 확인:

```text
http://localhost:8080/api/projects
```

## 프론트 재실행

```powershell
cd D:\pms_fullstack_clean_sb\frontend
npm run dev
```

브라우저:

```text
http://localhost:3000
```

## 실제 연결되는 API

```text
GET   /api/dashboard/summary
GET   /api/projects
POST  /api/projects
GET   /api/projects/{projectId}/requirements
POST  /api/projects/{projectId}/requirements
PATCH /api/requirements/{requirementId}/confirm
GET   /api/projects/{projectId}/wbs-tasks
GET   /api/my-work/wbs-tasks?assignee=김현대
PATCH /api/wbs-tasks/{taskId}/start
PATCH /api/wbs-tasks/{taskId}/complete
GET   /api/projects/{projectId}/test-cases
POST  /api/projects/{projectId}/test-cases
PATCH /api/test-cases/{testCaseId}/execute
GET   /api/projects/{projectId}/defects
```

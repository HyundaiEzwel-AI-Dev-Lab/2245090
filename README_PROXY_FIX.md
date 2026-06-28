# Nuxt Proxy Fix Patch

프론트에서 `[GET] "/api/projects": 500 Internal Server Error`가 날 때 적용하는 패치입니다.

## 원인

Nuxt devProxy가 불안정하게 동작하거나 `/api` 요청을 제대로 Spring Boot로 넘기지 못하면 프론트 기준으로 500이 뜰 수 있습니다.

## 적용 파일

```text
frontend/nuxt.config.ts
frontend/server/api/[...path].ts
```

## 동작

브라우저:
```text
http://localhost:3000/api/projects
```

Nuxt server route:
```text
frontend/server/api/[...path].ts
```

Spring Boot:
```text
http://localhost:8080/api/projects
```

으로 전달합니다.

## 적용 후

```powershell
cd D:\pms_fullstack_clean_sb\frontend
Ctrl + C
Remove-Item -Recurse -Force .nuxt -ErrorAction SilentlyContinue
npm run dev
```

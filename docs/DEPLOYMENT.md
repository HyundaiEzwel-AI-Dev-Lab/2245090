# 배포 가이드

## Local

```bash
docker compose up -d postgres
cd backend && gradle bootRun
cd frontend && npm install && npm run dev
```

## Docker Backend

```bash
docker compose -f docker-compose.full.yml up --build
```

## Frontend Vercel

- Framework: Nuxt.js
- Build Command: `npm run build`
- Output: Nuxt 기본값
- Environment Variable:
  - `NUXT_PUBLIC_API_BASE=https://백엔드주소/api`

## 주의

- Frontend만 Vercel에 올리면 DB/API는 동작하지 않는다.
- DB까지 보여주려면 Backend를 별도 서버/Render/Fly.io/Railway 등에 올리거나 사내 서버에서 실행해야 한다.

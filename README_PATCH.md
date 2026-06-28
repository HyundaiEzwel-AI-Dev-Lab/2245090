# PMS Frontend SB UI Patch

이 패치는 기존 프론트의 서비스 import 오류를 제거하고, SB 기준 내업무 UI를 더미 데이터 기반으로 먼저 정상 렌더링하기 위한 파일 세트입니다.

## 적용 파일

```text
frontend/nuxt.config.ts
frontend/app.vue
frontend/pages/index.vue
frontend/pages/my-work.vue
frontend/assets/css/main.css
```

## 실행

```powershell
cd D:\pms_fullstack_clean_sb\frontend
npm run dev
```

브라우저:

```text
http://localhost:3000
```

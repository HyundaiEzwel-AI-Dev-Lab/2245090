# 아키텍처

## 전체 구조

```text
frontend(Nuxt/Vue)
  Page
   ↓
  Component
   ↓
  Service
   ↓ REST API
backend(Spring Boot)
  Controller
   ↓
  Service
   ↓
  Repository
   ↓
PostgreSQL
```

## Backend 계층 규칙

| 계층 | 역할 | 금지 |
|---|---|---|
| Controller | 요청/응답 DTO, validation | 비즈니스 로직 직접 작성 금지 |
| Service | 트랜잭션, 업무 규칙 | EntityManager 직접 남용 금지 |
| Repository | DB 접근 | 화면 응답 DTO 조립 금지 |
| Entity | 도메인 상태 변경 메서드 | 무분별한 setter 금지 |
| DTO | API 계약 | Entity 그대로 외부 노출 금지 |

## Frontend 계층 규칙

| 계층 | 역할 | 금지 |
|---|---|---|
| Page | 화면 조합, 사용자 이벤트 시작점 | API 상세 URL 직접 작성 금지 |
| Component | 표시/입력 UI | API 호출 금지 |
| Service | API 호출 함수 | DOM 직접 조작 금지 |
| Types | API 응답/요청 타입 | any 남용 금지 |
| Utils | 날짜/상태 계산 | 화면 상태 직접 변경 금지 |

# 전사 프로젝트 관리 시스템 PMS Fullstack POC

SB 기반으로 만든 **Spring Boot + PostgreSQL + Nuxt 3(Vue) + TypeScript** 풀스택 POC입니다.

이 프로젝트는 운영 투입용 완성본이 아니라, 여러 명이 협업하며 실제 시스템으로 확장할 수 있도록 만든 **풀스택 베이스라인**입니다.

## 기술 스택

| 영역 | 기술 |
|---|---|
| Backend | Java 17, Spring Boot 3.3.5, Spring Data JPA, Flyway, PostgreSQL |
| Frontend | Nuxt 3.11.2, Vue 3, TypeScript |
| DB | PostgreSQL 16 |
| Local infra | Docker Compose |
| API | REST API, DTO 기반 요청/응답 |
| 구조 | Page → Component → Service → Repository 계층 분리 |

## 빠른 실행

### 1. DB 실행

```bash
docker compose up -d postgres
```

### 2. Backend 실행

```bash
cd backend
./gradlew bootRun
```

Windows에서 Gradle Wrapper 실행이 안 되면:

```powershell
cd backend
gradlew.bat bootRun
```

### 3. Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

접속:

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api

## 시연 흐름

1. 프로젝트 현황에서 프로젝트 조회/검색
2. 프로젝트 상세 진입
3. 요구사항 등록
4. 요구사항 확정
5. WBS 자동 생성 확인
6. WBS 일정 등록/착수/완료 처리
7. 내업무 화면에서 일정 반영 확인
8. 테스트 케이스 실패 처리
9. 결함 자동 생성 확인
10. 대시보드/진척률 확인

## 문서

- [SB 매핑](docs/SB_MAPPING.md)
- [아키텍처](docs/ARCHITECTURE.md)
- [코드 컨벤션](docs/CODE_CONVENTION.md)
- [API 명세](docs/API_SPEC.md)
- [DB 스키마](docs/DB_SCHEMA.md)
- [Cursor 프롬프트](docs/CURSOR_PROMPTS.md)
- [협업/브랜치 규칙](docs/GIT_CONVENTION.md)
- [개발 작업 분배](docs/TASK_SPLIT.md)

## 주의

- 인증/권한은 POC 수준의 Mock User 기반입니다.
- 회사 내부 인증/인사 DB 연동은 별도 확장 영역입니다.
- SB 전체 상세 팝업을 모두 구현한 것은 아니며, 핵심 업무 흐름을 우선 구현했습니다.

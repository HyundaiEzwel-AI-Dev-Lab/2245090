# Cursor 프롬프트 모음

## 1. 전체 구조 분석

```text
현재 프로젝트는 Spring Boot + PostgreSQL + Nuxt 3 + TypeScript 기반 PMS 풀스택 POC입니다.
코드를 수정하지 말고 먼저 전체 구조를 분석해주세요.

확인할 것:
1. backend의 Controller/Service/Repository/Entity 구조
2. frontend의 Page/Component/Service/Type 구조
3. SB 업무 흐름이 어떤 API로 연결되는지
4. Clean Code 관점에서 우선 리팩토링할 부분
5. 여러 명이 나눠 개발할 때 충돌 위험이 있는 파일
```

## 2. 기능 추가 기본 프롬프트

```text
아래 기능을 추가해주세요.

기능명:
요구사항:
관련 SB 메뉴:
수정 대상:

규칙:
1. Controller에는 비즈니스 로직을 넣지 마세요.
2. Service에서 트랜잭션을 관리하세요.
3. Repository는 DB 접근만 담당하게 하세요.
4. Frontend 화면에서는 API URL을 직접 작성하지 말고 service를 통해 호출하세요.
5. DTO/Type 변경 시 docs/API_SPEC.md도 함께 수정하세요.
6. DB 변경이 필요하면 Flyway migration 파일을 새로 추가하세요.

완료 후 수정 파일 목록과 테스트 방법을 알려주세요.
```

## 3. 요구사항 확정/WBS 보완

```text
요구사항 확정 시 WBS 생성 로직을 보완해주세요.

목표:
- 요구사항의 시스템구분/업무구분/화면명을 WBS에 승계
- 업무유형별 기본 담당자를 정책화
- 이미 WBS가 생성된 요구사항은 중복 생성 방지
- 생성 후 변경이력 적재

주의:
- RequirementService와 WbsService 책임을 분리하세요.
- Entity setter를 추가하지 말고 의미 있는 메서드를 사용하세요.
```

## 4. 프론트 반응형 개선

```text
Nuxt/Vue 화면의 PC/모바일 반응형을 개선해주세요.

기준:
- PC: 1180px 이상
- Tablet: 721px~1179px
- Mobile: 720px 이하

요구사항:
1. LNB는 모바일에서 햄버거 메뉴로 동작
2. 검색 조건은 모바일에서 1열
3. 테이블은 가로 스크롤 유지
4. 카드형 업무 목록은 모바일에서 세로 배치
5. 기존 API 연동 로직은 변경하지 않기
```

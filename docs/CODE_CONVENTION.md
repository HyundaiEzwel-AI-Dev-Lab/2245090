# 코드 컨벤션

## 공통

- 축약어보다 의미가 드러나는 이름을 사용한다.
- 함수는 한 가지 일만 한다.
- 30줄을 넘는 함수는 분리를 검토한다.
- 250줄을 넘는 Vue 컴포넌트는 하위 컴포넌트 분리를 검토한다.
- 중복 로직은 `utils`, `service`, 공통 컴포넌트로 분리한다.

## Java

### Naming

| 대상 | 규칙 | 예시 |
|---|---|---|
| Class | PascalCase | `ProjectService` |
| Method | camelCase | `confirmRequirement` |
| DTO | 요청/응답 구분 | `ProjectCreateRequest`, `ProjectResponse` |
| Entity | 단수 명사 | `Project`, `WbsTask` |
| Repository | Entity + Repository | `ProjectRepository` |

### Entity

- `@Setter` 전체 적용 금지
- 상태 변경은 의미 있는 메서드로 작성
- 예: `requirement.confirm()`, `wbsTask.complete()`

### Transaction

- 조회 서비스는 `@Transactional(readOnly = true)`
- 변경 서비스는 메서드에 `@Transactional`

## TypeScript/Vue

### Naming

| 대상 | 규칙 | 예시 |
|---|---|---|
| Vue Page | kebab route | `/my-work` |
| Component | PascalCase | `ProjectTable.vue` |
| Composable | use prefix | `useProjectService` |
| Type | PascalCase | `Project`, `WbsTask` |

### Vue

- API 호출은 `services`에서만 한다.
- 공통 버튼/카드/배지는 `components/common`을 사용한다.
- 화면 로직이 복잡해지면 composable로 분리한다.

## Commit Convention

| 타입 | 의미 |
|---|---|
| feat | 기능 추가 |
| fix | 버그 수정 |
| refactor | 기능 변경 없는 구조 개선 |
| docs | 문서 변경 |
| chore | 설정/빌드 변경 |
| test | 테스트 추가/수정 |

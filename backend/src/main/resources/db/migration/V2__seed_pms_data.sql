INSERT INTO pms_users(employee_no, name, department, role, active, created_at, updated_at) VALUES
('2245090', '이지윤', '테크담당', 'ADMIN', TRUE, NOW(), NOW()),
('1000001', '김현대', '개발팀', 'DEVELOPER', TRUE, NOW(), NOW()),
('1000002', '테스터', 'QA팀', 'TESTER', TRUE, NOW(), NOW());

INSERT INTO projects(project_code, name, department, requester, status, open_due_date, progress_rate, description, created_at, updated_at) VALUES
('PJ-2026-001', '전사 프로젝트 관리 시스템 구축', '웹기획팀', '이지윤', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '21 day', 35, 'SB 기반 PMS 구축 POC', NOW(), NOW()),
('PJ-2026-002', '주문취소 시 쿠폰 할인취소 정보 표기', '상품사업부', '김보성', 'TESTING', CURRENT_DATE + INTERVAL '7 day', 80, '주문/클레임 개선', NOW(), NOW()),
('PJ-2026-003', 'DL이앤씨 바우처 정책 변경', '테크담당', '전찬양', 'DISCUSSING', CURRENT_DATE + INTERVAL '30 day', 20, '바우처 정책 변경 대응', NOW(), NOW());

INSERT INTO requirements(project_id, title, system_type, business_type, menu_name, description, status, created_at, updated_at) VALUES
(1, '내업무 카드형/캘린더형 업무 조회', 'HIMS', '공통/API', '내업무', 'WBS 기준으로 배정 업무와 일정을 조회한다.', 'CONFIRMED', NOW(), NOW()),
(1, '요구사항 확정 시 WBS 자동 생성', 'HIMS', '공통/API', '요구사항 관리', '확정된 요구사항 기준으로 작업단위를 생성한다.', 'DRAFT', NOW(), NOW());

INSERT INTO wbs_tasks(project_id, requirement_id, task_name, work_type, assignee, plan_start_date, plan_end_date, progress_rate, status, priority, difficulty, unit_test_enabled, note, created_at, updated_at) VALUES
(1, 1, '내업무 화면 개발', 'DEVELOPMENT', '김현대', CURRENT_DATE - INTERVAL '2 day', CURRENT_DATE + INTERVAL '5 day', 45, 'IN_PROGRESS', '보통', '중', TRUE, '카드/캘린더 동기화 필요', NOW(), NOW()),
(1, 1, '내업무 단위테스트', 'UNIT_TEST', '테스터', CURRENT_DATE + INTERVAL '6 day', CURRENT_DATE + INTERVAL '8 day', 0, 'READY', '보통', '중', TRUE, NULL, NOW(), NOW());

INSERT INTO test_cases(project_id, wbs_task_id, stage, case_name, procedure, expected_result, result, tester, actual_result, created_at, updated_at) VALUES
(1, 1, 'UNIT', '내업무 목록 조회', '내업무 화면에 진입한다.', '배정된 WBS 업무가 노출된다.', 'NOT_RUN', NULL, NULL, NOW(), NOW());

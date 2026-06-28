CREATE TABLE pms_users (
    id BIGSERIAL PRIMARY KEY,
    employee_no VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    department VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    project_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    department VARCHAR(100) NOT NULL,
    requester VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    open_due_date DATE,
    progress_rate INTEGER NOT NULL DEFAULT 0,
    description VARCHAR(2000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE requirements (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    title VARCHAR(200) NOT NULL,
    system_type VARCHAR(50) NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    menu_name VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE wbs_tasks (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    requirement_id BIGINT REFERENCES requirements(id),
    task_name VARCHAR(200) NOT NULL,
    work_type VARCHAR(30) NOT NULL,
    assignee VARCHAR(50) NOT NULL,
    plan_start_date DATE,
    plan_end_date DATE,
    actual_start_date DATE,
    actual_end_date DATE,
    progress_rate INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    unit_test_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    note VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE test_cases (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    wbs_task_id BIGINT REFERENCES wbs_tasks(id),
    stage VARCHAR(30) NOT NULL,
    case_name VARCHAR(200) NOT NULL,
    procedure VARCHAR(1000) NOT NULL,
    expected_result VARCHAR(1000) NOT NULL,
    result VARCHAR(30) NOT NULL,
    tester VARCHAR(50),
    actual_result VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE defects (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    test_case_id BIGINT REFERENCES test_cases(id),
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    assignee VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    resolution VARCHAR(2000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE change_histories (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    changed_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_requirements_project_id ON requirements(project_id);
CREATE INDEX idx_wbs_project_id ON wbs_tasks(project_id);
CREATE INDEX idx_wbs_assignee ON wbs_tasks(assignee);
CREATE INDEX idx_test_cases_project_id ON test_cases(project_id);
CREATE INDEX idx_defects_project_id ON defects(project_id);
CREATE INDEX idx_change_histories_project_id ON change_histories(project_id);

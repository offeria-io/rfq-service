CREATE TABLE work_locations (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    code VARCHAR(100) NOT NULL,
    name_en VARCHAR(255) NOT NULL,
    name_ar VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_work_locations_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_work_locations_project_code
        UNIQUE (project_id, code)
);

CREATE INDEX idx_work_locations_project_id
    ON work_locations(project_id);

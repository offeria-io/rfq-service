CREATE TABLE projects (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    name_en VARCHAR(255) NOT NULL,
    name_ar VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_projects_client
        FOREIGN KEY (client_id)
        REFERENCES clients(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_projects_client_code
        UNIQUE (client_id, code)
);

CREATE INDEX idx_projects_client_id
    ON projects(client_id);

ALTER TABLE rfqs
    ADD COLUMN rfq_number VARCHAR(150);

ALTER TABLE rfqs
    ADD COLUMN client_id UUID;

ALTER TABLE rfqs
    ADD COLUMN project_id UUID;

ALTER TABLE rfqs
    ADD COLUMN contract_id UUID;

ALTER TABLE rfqs
    ADD COLUMN work_location_id UUID;

ALTER TABLE rfqs
    ADD COLUMN attention_contact_id UUID;

ALTER TABLE rfqs
    ADD CONSTRAINT fk_rfqs_client
        FOREIGN KEY (client_id)
        REFERENCES clients(id);

ALTER TABLE rfqs
    ADD CONSTRAINT fk_rfqs_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id);

ALTER TABLE rfqs
    ADD CONSTRAINT fk_rfqs_contract
        FOREIGN KEY (contract_id)
        REFERENCES contracts(id);

ALTER TABLE rfqs
    ADD CONSTRAINT fk_rfqs_work_location
        FOREIGN KEY (work_location_id)
        REFERENCES work_locations(id);

ALTER TABLE rfqs
    ADD CONSTRAINT fk_rfqs_attention_contact
        FOREIGN KEY (attention_contact_id)
        REFERENCES attention_contacts(id);

CREATE INDEX idx_rfqs_client_id
    ON rfqs(client_id);

CREATE INDEX idx_rfqs_project_id
    ON rfqs(project_id);

CREATE INDEX idx_rfqs_contract_id
    ON rfqs(contract_id);

CREATE INDEX idx_rfqs_work_location_id
    ON rfqs(work_location_id);

CREATE INDEX idx_rfqs_attention_contact_id
    ON rfqs(attention_contact_id);

CREATE UNIQUE INDEX uk_rfqs_client_rfq_number
    ON rfqs(client_id, rfq_number);

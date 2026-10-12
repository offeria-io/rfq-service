ALTER TABLE rfqs
    ADD COLUMN intake_source VARCHAR(20)
    NOT NULL DEFAULT 'MANUAL';

ALTER TABLE rfqs
    ADD CONSTRAINT chk_rfqs_intake_source
    CHECK (intake_source IN ('MANUAL', 'FILE'));

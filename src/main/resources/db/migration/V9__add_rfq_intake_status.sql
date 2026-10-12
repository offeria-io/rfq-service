ALTER TABLE rfqs
    ADD COLUMN intake_status VARCHAR(30)
    NOT NULL DEFAULT 'PENDING_REVIEW';

ALTER TABLE rfqs
    ADD CONSTRAINT chk_rfqs_intake_status
    CHECK (intake_status IN (
        'PENDING_REVIEW',
        'VALIDATED',
        'REJECTED'
    ));

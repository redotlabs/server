ALTER TABLE consultations
    ADD COLUMN IF NOT EXISTS remark VARCHAR(1000);

ALTER TABLE consultations
    DROP CONSTRAINT IF EXISTS ck_consultation_status;

ALTER TABLE consultations
    ADD CONSTRAINT ck_consultation_status
        CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'));

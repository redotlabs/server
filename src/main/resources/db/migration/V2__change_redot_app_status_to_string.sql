-- Convert redot_apps.status from integer ordinal to enum text values
ALTER TABLE redot_apps
    DROP CONSTRAINT IF EXISTS redot_apps_status_check;

ALTER TABLE redot_apps
    ADD COLUMN status_new VARCHAR(50);

UPDATE redot_apps
SET status_new = CASE status
    WHEN 0 THEN 'ACTIVE'
    WHEN 1 THEN 'INACTIVE'
    WHEN 2 THEN 'DELETED'
    WHEN 3 THEN 'RESIGNED'
    WHEN 4 THEN 'PAYMENT_DELAYED'
END;

ALTER TABLE redot_apps
    ALTER COLUMN status_new SET NOT NULL;

ALTER TABLE redot_apps
    DROP COLUMN status;

ALTER TABLE redot_apps
    RENAME COLUMN status_new TO status;

ALTER TABLE redot_apps
    ADD CONSTRAINT redot_apps_status_check
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'DELETED', 'RESIGNED', 'PAYMENT_DELAYED'));

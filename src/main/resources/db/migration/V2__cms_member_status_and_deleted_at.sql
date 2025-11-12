-- Adds soft-delete related columns to cms_members and backfills existing data
ALTER TABLE cms_members
    ADD COLUMN IF NOT EXISTS status VARCHAR(20);

ALTER TABLE cms_members
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

UPDATE cms_members
SET status = 'ACTIVE'
WHERE status IS NULL;

ALTER TABLE cms_members
    ALTER COLUMN status SET DEFAULT 'ACTIVE';

ALTER TABLE cms_members
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE redot_members
    DROP CONSTRAINT IF EXISTS redot_members_status_check;

ALTER TABLE redot_members
    ADD CONSTRAINT redot_members_status_check CHECK (status IN ('ACTIVE', 'BANNED', 'DELETED'));

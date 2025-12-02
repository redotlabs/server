-- Allow OWNER role in cms_members role check constraint
ALTER TABLE cms_members
    DROP CONSTRAINT IF EXISTS cms_members_role_check;

ALTER TABLE cms_members
    ADD CONSTRAINT cms_members_role_check
        CHECK (role IN ('ADMIN', 'MANAGER', 'OWNER'));

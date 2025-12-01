ALTER TABLE redot_apps RENAME COLUMN app_name TO name;

CREATE UNIQUE INDEX IF NOT EXISTS ux_cms_members_owner_per_redot_app
    ON cms_members (redot_app_id)
    WHERE role = 'OWNER' AND status = 'ACTIVE';

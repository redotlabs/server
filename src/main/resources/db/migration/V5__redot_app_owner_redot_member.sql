ALTER TABLE redot_apps
    ADD COLUMN redot_member_id BIGINT;

INSERT INTO redot_members (email, password, name, profile_image_url, status, social_provider, social_provider_id, created_at, deleted_at)
SELECT DISTINCT ON (cm.email)
       cm.email,
       cm.password,
       cm.name,
       cm.profile_image_url,
       cm.status,
       NULL,
       NULL,
       cm.created_at,
       cm.deleted_at
FROM cms_members cm
JOIN redot_apps ra ON ra.owner_id = cm.id
ORDER BY cm.email, cm.id
ON CONFLICT (email) DO NOTHING;

UPDATE redot_apps ra
SET redot_member_id = rm.id
FROM cms_members cm
JOIN redot_members rm ON rm.email = cm.email
WHERE ra.owner_id = cm.id;

ALTER TABLE redot_apps
    ALTER COLUMN redot_member_id SET NOT NULL;

ALTER TABLE redot_apps
    ADD CONSTRAINT fk_redot_apps_redot_member
        FOREIGN KEY (redot_member_id) REFERENCES redot_members (id);

ALTER TABLE redot_apps
    DROP CONSTRAINT IF EXISTS fk_redot_apps_owner;

ALTER TABLE redot_apps
    DROP COLUMN owner_id;

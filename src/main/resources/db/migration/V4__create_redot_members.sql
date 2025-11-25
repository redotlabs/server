CREATE TABLE IF NOT EXISTS redot_members (
    id                  BIGSERIAL PRIMARY KEY,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            VARCHAR(255),
    name                VARCHAR(255) NOT NULL,
    profile_image_url   VARCHAR(255),
    status              VARCHAR(50)  NOT NULL,
    social_provider     VARCHAR(50),
    social_provider_id  VARCHAR(255),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP,
    CONSTRAINT uk_redot_members_social UNIQUE (social_provider, social_provider_id)
);

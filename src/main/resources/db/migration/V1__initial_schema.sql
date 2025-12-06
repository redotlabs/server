-- Consolidated initial schema for the Redot service

CREATE TABLE IF NOT EXISTS admins (
    id                BIGSERIAL PRIMARY KEY,
    email             VARCHAR(255) NOT NULL UNIQUE,
    name              VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    status            VARCHAR(20)  NOT NULL,
    password          VARCHAR(255) NOT NULL,
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMP,
    CONSTRAINT admins_status_check CHECK (status IN ('ACTIVE', 'DELETED'))
);

CREATE TABLE IF NOT EXISTS redot_members (
    id                 BIGSERIAL PRIMARY KEY,
    email              VARCHAR(255) NOT NULL UNIQUE,
    password           VARCHAR(255),
    name               VARCHAR(255) NOT NULL,
    profile_image_url  VARCHAR(255),
    status             VARCHAR(50)  NOT NULL,
    social_provider    VARCHAR(50),
    social_provider_id VARCHAR(255),
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at         TIMESTAMP,
    CONSTRAINT uk_redot_members_social UNIQUE (social_provider, social_provider_id),
    CONSTRAINT redot_members_status_check CHECK (status IN ('ACTIVE', 'DELETED')),
    CONSTRAINT redot_members_social_provider_check CHECK (social_provider IN ('GOOGLE'))
);

CREATE TABLE IF NOT EXISTS app_plans (
    id             BIGSERIAL PRIMARY KEY,
    plan_type      VARCHAR(50)    NOT NULL UNIQUE,
    display_name   VARCHAR(100)   NOT NULL,
    description    TEXT,
    price          DECIMAL(10, 2) NOT NULL,
    max_page_views BIGINT         NOT NULL,
    max_pages      INTEGER        NOT NULL,
    max_managers   INTEGER        NOT NULL,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT app_plans_plan_type_check CHECK (plan_type IN ('FREE', 'PRO', 'ENTERPRISE'))
);

CREATE TABLE IF NOT EXISTS cms_menus (
    id         BIGSERIAL PRIMARY KEY,
    plan_id    BIGINT       NOT NULL REFERENCES app_plans (id) ON DELETE CASCADE,
    name       VARCHAR(100) NOT NULL,
    path       VARCHAR(255) NOT NULL,
    sort_order INTEGER      NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cms_menus_plan_path UNIQUE (plan_id, path),
    CONSTRAINT uk_cms_menus_plan_order UNIQUE (plan_id, sort_order)
);

CREATE TABLE IF NOT EXISTS redot_apps (
    id                  BIGSERIAL PRIMARY KEY,
    redot_member_id     BIGINT REFERENCES redot_members (id),
    plan_id             BIGINT NOT NULL REFERENCES app_plans (id),
    status              INTEGER      NOT NULL,
    name                VARCHAR(255) NOT NULL,
    is_created_manager  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT redot_apps_status_check CHECK (status IN (0, 1, 2, 3, 4))
);

CREATE TABLE IF NOT EXISTS cms_members (
    id                BIGSERIAL PRIMARY KEY,
    redot_app_id      BIGINT       NOT NULL REFERENCES redot_apps (id),
    name              VARCHAR(255) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    password          VARCHAR(255) NOT NULL,
    role              VARCHAR(50)  NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    deleted_at        TIMESTAMP,
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cms_members_redot_app_email UNIQUE (redot_app_id, email),
    CONSTRAINT cms_members_role_check CHECK (role IN ('ADMIN', 'MANAGER', 'OWNER')),
    CONSTRAINT cms_members_status_check CHECK (status IN ('ACTIVE', 'DELETED'))
);

CREATE TABLE IF NOT EXISTS domains (
    id            BIGSERIAL PRIMARY KEY,
    subdomain     VARCHAR(255) NOT NULL UNIQUE,
    custom_domain VARCHAR(255) UNIQUE,
    redot_app_id  BIGINT UNIQUE REFERENCES redot_apps (id),
    reserved      BOOLEAN      NOT NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS style_info (
    id           BIGSERIAL PRIMARY KEY,
    redot_app_id BIGINT       NOT NULL UNIQUE REFERENCES redot_apps (id),
    font         VARCHAR(255) NOT NULL,
    color        VARCHAR(255) NOT NULL,
    theme        VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT style_info_theme_check CHECK (theme IN ('MODERN', 'DEFAULT'))
);

CREATE TABLE IF NOT EXISTS site_settings (
    id           BIGSERIAL PRIMARY KEY,
    redot_app_id BIGINT       NOT NULL UNIQUE REFERENCES redot_apps (id),
    logo_url     VARCHAR(255),
    site_name    VARCHAR(255),
    ga_info      VARCHAR(255),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS redot_app_inquiries (
    id              BIGSERIAL PRIMARY KEY,
    redot_app_id    BIGINT       NOT NULL REFERENCES redot_apps (id),
    inquiry_number  VARCHAR(255) NOT NULL UNIQUE,
    inquirer_name   VARCHAR(255) NOT NULL,
    status          VARCHAR(50)  NOT NULL,
    title           VARCHAR(255) NOT NULL,
    content         TEXT         NOT NULL,
    assignee_id     BIGINT REFERENCES cms_members (id),
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT redot_app_inquiries_status_check CHECK (status IN ('UNPROCESSED', 'COMPLETED'))
);

CREATE TABLE IF NOT EXISTS inquiry_sequences (
    id                   BIGSERIAL PRIMARY KEY,
    last_sequence_number BIGINT NOT NULL,
    inquiry_date         DATE   NOT NULL UNIQUE,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS consultations (
    id                  BIGSERIAL PRIMARY KEY,
    email               VARCHAR(255) NOT NULL,
    phone               VARCHAR(255),
    content             VARCHAR(1000) NOT NULL,
    current_website_url VARCHAR(255),
    page                VARCHAR(255),
    status              VARCHAR(50) NOT NULL,
    type                VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_consultation_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED')),
    CONSTRAINT ck_consultation_type CHECK (type IN ('NEW', 'RENEWAL'))
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_cms_members_owner_per_redot_app
    ON cms_members (redot_app_id)
    WHERE role = 'OWNER' AND status = 'ACTIVE';

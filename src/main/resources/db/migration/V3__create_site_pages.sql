CREATE TABLE IF NOT EXISTS app_pages
(
    id           BIGSERIAL PRIMARY KEY,
    redot_app_id BIGINT        NOT NULL REFERENCES redot_apps (id),
    content      TEXT          NOT NULL,
    path         VARCHAR(255)  NOT NULL,
    is_protected BOOLEAN       NOT NULL,
    description  TEXT,
    title        VARCHAR(100)  NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS app_versions
(
    id               BIGSERIAL PRIMARY KEY,
    redot_app_id     BIGINT       NOT NULL REFERENCES redot_apps (id),
    remark           TEXT,
    status           VARCHAR(50)  NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE app_versions
    ADD CONSTRAINT ck_app_versions_status CHECK (status IN ('PUBLISHED', 'DRAFT', 'PREVIOUS'));

CREATE TABLE IF NOT EXISTS app_version_pages
(
    id             BIGSERIAL PRIMARY KEY,
    app_version_id BIGINT NOT NULL REFERENCES app_versions (id) ON DELETE RESTRICT,
    app_page_id    BIGINT NOT NULL REFERENCES app_pages (id) ON DELETE CASCADE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_app_version_page UNIQUE (app_version_id, app_page_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_app_versions_published_per_app
    ON app_versions (redot_app_id)
        WHERE status = 'PUBLISHED';

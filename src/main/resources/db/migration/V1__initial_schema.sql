-- Baseline schema prior to introducing Flyway-managed changes
-- Generated from current JPA entities (without CMS member soft-delete columns)

CREATE TABLE IF NOT EXISTS admins (
    id           BIGSERIAL PRIMARY KEY,
    email        VARCHAR(255) NOT NULL UNIQUE,
    name         VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    status       VARCHAR(20)  NOT NULL,
    password     VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customers (
    id            BIGSERIAL PRIMARY KEY,
    owner_id      BIGINT UNIQUE,
    status        INTEGER      NOT NULL,
    company_name  VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cms_members (
    id               BIGSERIAL PRIMARY KEY,
    customer_id      BIGINT       NOT NULL,
    name             VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    password         VARCHAR(255) NOT NULL,
    role             VARCHAR(50)  NOT NULL,
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cms_members_customer_email UNIQUE (customer_id, email)
);

CREATE TABLE IF NOT EXISTS domains (
    id            BIGSERIAL PRIMARY KEY,
    subdomain     VARCHAR(255) NOT NULL UNIQUE,
    custom_domain VARCHAR(255) UNIQUE,
    customer_id   BIGINT UNIQUE,
    reserved      BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS style_info (
    id           BIGSERIAL PRIMARY KEY,
    customer_id  BIGINT       NOT NULL UNIQUE,
    font         VARCHAR(255) NOT NULL,
    color        VARCHAR(255) NOT NULL,
    theme        VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS site_settings (
    id           BIGSERIAL PRIMARY KEY,
    customer_id  BIGINT       NOT NULL UNIQUE,
    logo_url     VARCHAR(255),
    site_name    VARCHAR(255),
    ga_info      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS customer_inquiries (
    id             BIGSERIAL PRIMARY KEY,
    customer_id    BIGINT       NOT NULL,
    inquiry_number VARCHAR(255) NOT NULL UNIQUE,
    inquirer_name  VARCHAR(255) NOT NULL,
    status         VARCHAR(50)  NOT NULL,
    title          VARCHAR(255) NOT NULL,
    content        TEXT         NOT NULL,
    assignee_id    BIGINT,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inquiry_sequences (
    id                  BIGSERIAL PRIMARY KEY,
    last_sequence_number BIGINT NOT NULL,
    inquiry_date         DATE   NOT NULL UNIQUE
);

ALTER TABLE cms_members
    ADD CONSTRAINT fk_cms_members_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE customers
    ADD CONSTRAINT fk_customers_owner
        FOREIGN KEY (owner_id) REFERENCES cms_members (id);

ALTER TABLE domains
    ADD CONSTRAINT fk_domains_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE style_info
    ADD CONSTRAINT fk_style_info_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE site_settings
    ADD CONSTRAINT fk_site_settings_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE customer_inquiries
    ADD CONSTRAINT fk_customer_inquiries_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE customer_inquiries
    ADD CONSTRAINT fk_customer_inquiries_assignee
        FOREIGN KEY (assignee_id) REFERENCES cms_members (id);

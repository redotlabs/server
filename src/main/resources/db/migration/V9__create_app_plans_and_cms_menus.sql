-- 플랜 테이블
CREATE TABLE IF NOT EXISTS app_plans (
    id                BIGSERIAL PRIMARY KEY,
    plan_type         VARCHAR(50)    NOT NULL UNIQUE,
    display_name      VARCHAR(100)   NOT NULL,
    description       TEXT,
    price             DECIMAL(10, 2) NOT NULL,
    max_page_views    BIGINT         NOT NULL,
    max_pages         INTEGER        NOT NULL,
    max_managers      INTEGER        NOT NULL,
    created_at        TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
);

-- CMS 메뉴 테이블
CREATE TABLE IF NOT EXISTS cms_menus (
    id                BIGSERIAL PRIMARY KEY,
    plan_id           BIGINT         NOT NULL REFERENCES app_plans(id) ON DELETE CASCADE,
    name              VARCHAR(100)   NOT NULL,
    path              VARCHAR(255)   NOT NULL,
    sort_order        INTEGER        NOT NULL,
    created_at        TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_cms_menus_plan_path UNIQUE (plan_id, path),
    CONSTRAINT uk_cms_menus_plan_order UNIQUE (plan_id, sort_order)
);

-- redot_apps 테이블 plan_id 추가
ALTER TABLE redot_apps 
ADD COLUMN IF NOT EXISTS plan_id BIGINT REFERENCES app_plans(id);


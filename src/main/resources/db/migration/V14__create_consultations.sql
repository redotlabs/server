CREATE TABLE consultations (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    content VARCHAR(1000) NOT NULL,
    current_website_url VARCHAR(255),
    page VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_consultation_status CHECK (status IN ('PENDING','IN_PROGRESS','COMPLETED')),
    CONSTRAINT ck_consultation_type CHECK (type IN ('NEW', 'RENEWAL'))
);

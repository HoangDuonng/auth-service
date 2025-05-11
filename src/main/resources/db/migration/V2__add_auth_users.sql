-- V2__add_auth_users.sql
START TRANSACTION;

-- Bảng chỉ dùng để xác thực
CREATE TABLE IF NOT EXISTS auth_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    password VARCHAR(255),
    refresh_token TEXT,
    token_expiry TIMESTAMP,

    email_verification_token TEXT,
    email_verification_token_expired_at TIMESTAMP,

    reset_password_token TEXT,
    reset_password_token_expired_at TIMESTAMP,

    provider VARCHAR(50) DEFAULT 'local',
    provider_id VARCHAR(100),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (provider, provider_id),
    CHECK (
        (provider = 'local' AND password IS NOT NULL)
        OR
        (provider != 'local' AND password IS NULL)
    )
);

-- Chỉ mục hỗ trợ
CREATE INDEX IF NOT EXISTS idx_auth_users_user_id ON auth_users(user_id);
CREATE INDEX IF NOT EXISTS idx_auth_users_provider ON auth_users(provider);
CREATE INDEX IF NOT EXISTS idx_auth_users_provider_id ON auth_users(provider_id);
CREATE INDEX IF NOT EXISTS idx_email_verification_token ON auth_users(email_verification_token);
CREATE INDEX IF NOT EXISTS idx_reset_password_token ON auth_users(reset_password_token);
CREATE INDEX IF NOT EXISTS idx_auth_users_created_at ON auth_users(created_at);

COMMIT;
 
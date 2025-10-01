-- V1__init_schema.sql
BEGIN;

-- Drop existing table if exists
DROP TABLE IF EXISTS public.auth_users CASCADE;

-- Create auth_users table
CREATE TABLE public.auth_users (
    id BIGSERIAL PRIMARY KEY,
    document_id UUID DEFAULT gen_random_uuid() UNIQUE NOT NULL,
    user_id INTEGER NOT NULL UNIQUE,
    password VARCHAR(255),
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(100),
    refresh_token TEXT,
    token_expiry TIMESTAMP,
    email_verification_token TEXT,
    email_verification_token_expired_at TIMESTAMP,
    reset_password_token TEXT,
    reset_password_token_expired_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_auth_users_user_id ON public.auth_users(user_id);
CREATE INDEX idx_auth_users_provider ON public.auth_users(provider);
CREATE INDEX idx_auth_users_refresh_token ON public.auth_users(refresh_token);
CREATE INDEX idx_auth_users_email_verification_token ON public.auth_users(email_verification_token);
CREATE INDEX idx_auth_users_reset_password_token ON public.auth_users(reset_password_token);
CREATE INDEX idx_auth_users_document_id ON public.auth_users(document_id);

COMMIT;

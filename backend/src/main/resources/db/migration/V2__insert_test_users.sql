-- ============================================================
-- V2__insert_test_users.sql
-- Insert test users for development
-- ============================================================

-- Insert SUPER_ADMIN user (password: password123)
-- Hash: $2a$10$V5zRFB/lCWlDk4T5WfZsKusczKL8i0V/.wVb0Wl0hHJPJJqq1wLn2
INSERT INTO users (
    id,
    role,
    first_name,
    last_name,
    email,
    password_hash,
    phone,
    is_active,
    mfa_enabled,
    created_at,
    updated_at
) VALUES (
    gen_random_uuid(),
    'SUPER_ADMIN',
    'Admin',
    'User',
    'admin@deliveryos.fr',
    '$2a$10$V5zRFB/lCWlDk4T5WfZsKusczKL8i0V/.wVb0Wl0hHJPJJqq1wLn2',
    '+33612345678',
    true,
    false,
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Insert DISPATCHER user (password: password123)
INSERT INTO users (
    id,
    role,
    first_name,
    last_name,
    email,
    password_hash,
    phone,
    is_active,
    mfa_enabled,
    created_at,
    updated_at
) VALUES (
    gen_random_uuid(),
    'DISPATCHER',
    'Dispatcher',
    'User',
    'dispatcher@deliveryos.fr',
    '$2a$10$V5zRFB/lCWlDk4T5WfZsKusczKL8i0V/.wVb0Wl0hHJPJJqq1wLn2',
    '+33612345679',
    true,
    false,
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Insert DRIVER user (password: password123)
INSERT INTO users (
    id,
    role,
    first_name,
    last_name,
    email,
    password_hash,
    phone,
    is_active,
    mfa_enabled,
    created_at,
    updated_at
) VALUES (
    gen_random_uuid(),
    'DRIVER',
    'John',
    'Driver',
    'driver@deliveryos.fr',
    '$2a$10$V5zRFB/lCWlDk4T5WfZsKusczKL8i0V/.wVb0Wl0hHJPJJqq1wLn2',
    '+33612345680',
    true,
    false,
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

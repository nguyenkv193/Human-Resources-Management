INSERT INTO roles (name, description)
VALUES
    ('ADMIN', 'Quản trị toàn bộ hệ thống'),
    ('USER', 'Người dùng thông thường')
ON CONFLICT (name) DO NOTHING;

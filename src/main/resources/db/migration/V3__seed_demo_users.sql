-- Development-only demo accounts. Change their passwords before using any shared environment.
INSERT INTO users (username, password_hash, enabled)
VALUES
    ('hradmin', '$2a$10$2CZ2EMq90d0MgyOHAfp2eOeu2Cr82M02rKH2KAc/QXSZc/Mzd32hy', TRUE),
    ('employee', '$2a$10$b6AE9fBEw72vZj4fSEWNXOHUZCOZf1wH8TLWjUypJ62vkZjdvNbyO', TRUE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM (VALUES
    ('hradmin', 'ADMIN'),
    ('employee', 'USER')
) AS demo_accounts(username, role_name)
JOIN users u ON u.username = demo_accounts.username
JOIN roles r ON r.name = demo_accounts.role_name
ON CONFLICT (user_id, role_id) DO NOTHING;

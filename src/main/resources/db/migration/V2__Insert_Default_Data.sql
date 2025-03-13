INSERT INTO users (email, password) VALUES
('charantejreddyn@gmail.com', '$2a$12$w6q.b64XaHIFOj1Vt3wXVehhXYJEUXQBGqEKMdAqeUno0tge7t4gm'),
('vestasadmin@gmail.com', '$2a$12$3u.fAibWTYMnCe0FBAvf/OW6WVx2nVwHUL7K2DGmFOSJZ7fu/QZmK');

INSERT INTO roles (name) VALUES
('USER'),
('OWNER');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'charantejreddyn@gmail.com' AND r.name = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'vestasadmin@gmail.com' AND r.name = 'OWNER';
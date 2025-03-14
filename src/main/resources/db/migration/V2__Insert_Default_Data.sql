INSERT INTO users (name, email, password) VALUES
('Charan','charantejreddyn@gmail.com', '$2a$12$w6q.b64XaHIFOj1Vt3wXVehhXYJEUXQBGqEKMdAqeUno0tge7t4gm'),
('Vestas','vestasadmin@gmail.com', '$2a$12$3u.fAibWTYMnCe0FBAvf/OW6WVx2nVwHUL7K2DGmFOSJZ7fu/QZmK'),
('TestUser1','testuser1@gmail.com', '$2a$12$w6q.b64XaHIFOj1Vt3wXVehhXYJEUXQBGqEKMdAqeUno0tge7t4gm'),
('TestUser2','testuser2@gmail.com', '$2a$12$w6q.b64XaHIFOj1Vt3wXVehhXYJEUXQBGqEKMdAqeUno0tge7t4gm'),
('TestUser3','testuser3@gmail.com', '$2a$12$w6q.b64XaHIFOj1Vt3wXVehhXYJEUXQBGqEKMdAqeUno0tge7t4gm');

INSERT INTO roles (name) VALUES
('USER'),
('OWNER');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'charantejreddyn@gmail.com' AND r.name = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'testuser1@gmail.com' AND r.name = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'testuser2@gmail.com' AND r.name = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'testuser3@gmail.com' AND r.name = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'vestasadmin@gmail.com' AND r.name = 'OWNER';
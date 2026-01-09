-- ROLES
INSERT INTO roles (name) VALUES ('CUSTOMER') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO roles (name) VALUES ('SELLER') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO roles (name) VALUES ('ADMIN') ON DUPLICATE KEY UPDATE name = name;

-- USERS (exemplo)
INSERT INTO users (name, nickname, email, password)
VALUES
    ('Seller One', 'seller1', 'seller1@meli.com', '123456'),
    ('Seller Two', 'seller2', 'seller2@meli.com', '123456'),
    ('User One', 'user1', 'user1@meli.com', '123456'),
    ('Admin', 'admin', 'admin@meli.com', '123456');

-- USER_ROLE (exemplo)
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'SELLER'
WHERE u.nickname IN ('seller1', 'seller2')
    ON DUPLICATE KEY UPDATE user_id = user_id;

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'CUSTOMER'
WHERE u.nickname = 'user1'
    ON DUPLICATE KEY UPDATE user_id = user_id;

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ADMIN'
WHERE u.nickname = 'admin'
    ON DUPLICATE KEY UPDATE user_id = user_id;

-- CATEGORIES
INSERT INTO categories (name) VALUES ('Cadeiras') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO categories (name) VALUES ('Teclados') ON DUPLICATE KEY UPDATE name = name;

-- PRODUCTS (exemplo)
INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT
    'Cadeira Gamer',
    'Gamer',
    'Racer',
    'Red & Black',
    c.id,
    1500.50,
    1,
    u.id
FROM categories c
         JOIN users u ON u.nickname = 'seller1'
WHERE c.name = 'Cadeiras';

-- FOLLOWERS (exemplo: user1 segue seller1)
INSERT INTO followers (user_id, seller_id)
SELECT u.id, s.id
FROM users u
         JOIN users s ON s.nickname = 'seller1'
WHERE u.nickname = 'user1'
    ON DUPLICATE KEY UPDATE user_id = user_id;

-- Atualiza contadores (opcional simples para seed)
UPDATE users seller
SET followers_count = (
    SELECT COUNT(*) FROM followers f WHERE f.seller_id = seller.id
);

UPDATE users u
SET following_count = (
    SELECT COUNT(*) FROM followers f WHERE f.user_id = u.id
);
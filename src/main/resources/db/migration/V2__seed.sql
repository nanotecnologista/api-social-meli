-- ROLES (somente CUSTOMER e SELLER)
INSERT INTO roles (name) VALUES ('CUSTOMER') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO roles (name) VALUES ('SELLER') ON DUPLICATE KEY UPDATE name = name;

-- USERS (1 customer apenas, 1 customer + seller)
INSERT INTO users (name, nickname, email, password)
VALUES
    ('Customer One', 'customer1', 'customer1@meli.com', '123456'),
    ('Seller One', 'seller1', 'seller1@meli.com', '123456');

-- USER_ROLE: CUSTOMER para ambos
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'CUSTOMER'
WHERE u.nickname IN ('customer1', 'seller1')
    ON DUPLICATE KEY UPDATE user_id = user_id;

-- USER_ROLE: SELLER somente para seller1 (mas seller1 já tem CUSTOMER acima)
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'SELLER'
WHERE u.nickname = 'seller1'
    ON DUPLICATE KEY UPDATE user_id = user_id;

-- FOLLOWERS (customer1 segue seller1)
INSERT INTO followers (user_id, seller_id)
SELECT u.id, s.id
FROM users u
         JOIN users s ON s.nickname = 'seller1'
WHERE u.nickname = 'customer1'
    ON DUPLICATE KEY UPDATE user_id = user_id;

-- CATEGORY
INSERT INTO categories (name) VALUES ('Cadeiras') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO categories (name) VALUES ('Teclados') ON DUPLICATE KEY UPDATE name = name;

-- PRODUCT (seller1)
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

-- Atualiza contadores (opcional, mas útil pro retorno rápido)
UPDATE users seller
SET followers_count = (
    SELECT COUNT(*) FROM followers f WHERE f.seller_id = seller.id
);

UPDATE users u
SET following_count = (
    SELECT COUNT(*) FROM followers f WHERE f.user_id = u.id
);
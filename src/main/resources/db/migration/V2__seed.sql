-- ROLES (somente CUSTOMER e SELLER)
INSERT INTO roles (name) VALUES ('CUSTOMER') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO roles (name) VALUES ('SELLER') ON DUPLICATE KEY UPDATE name = name;

-- USERS - Variados cenários
INSERT INTO users (name, nickname, email, password)
VALUES
    -- Customers apenas (5)
    ('Ana Silva', 'customer_ana', 'ana@meli.com', '123456'),
    ('Bruno Costa', 'customer_bruno', 'bruno@meli.com', '123456'),
    ('Carla Mendes', 'customer_carla', 'carla@meli.com', '123456'),
    ('Diego Santos', 'customer_diego', 'diego@meli.com', '123456'),
    ('Elisa Rocha', 'customer_elisa', 'elisa@meli.com', '123456'),
    
    -- Sellers com muitos seguidores (3)
    ('João Popular', 'seller_joao_popular', 'joao.popular@meli.com', '123456'),
    ('Maria Famosa', 'seller_maria_famosa', 'maria.famosa@meli.com', '123456'),
    ('Pedro Top', 'seller_pedro_top', 'pedro.top@meli.com', '123456'),
    
    -- Sellers com poucos seguidores (2)
    ('Lucas Iniciante', 'seller_lucas_iniciante', 'lucas@meli.com', '123456'),
    ('Sofia Nova', 'seller_sofia_nova', 'sofia@meli.com', '123456'),
    
    -- Seller sem nenhuma publicação (1)
    ('Rafael Sem Posts', 'seller_rafael_semposts', 'rafael@meli.com', '123456'),
    
    -- Seller sem seguidores (1)
    ('Camila Sozinha', 'seller_camila_sozinha', 'camila@meli.com', '123456'),
    
    -- Usuários com ambos perfis (CUSTOMER + SELLER) (4)
    ('Roberto Dual', 'dual_roberto', 'roberto@meli.com', '123456'),
    ('Juliana Dual', 'dual_juliana', 'juliana@meli.com', '123456'),
    ('Fernando Dual', 'dual_fernando', 'fernando@meli.com', '123456'),
    ('Patricia Dual', 'dual_patricia', 'patricia@meli.com', '123456');

-- USER_ROLE: CUSTOMER para todos os customers
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'CUSTOMER'
WHERE u.nickname LIKE 'customer_%'
ON DUPLICATE KEY UPDATE user_id = user_id;

-- USER_ROLE: SELLER para todos os sellers
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'SELLER'
WHERE u.nickname LIKE 'seller_%'
ON DUPLICATE KEY UPDATE user_id = user_id;

-- USER_ROLE: CUSTOMER para usuários dual
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'CUSTOMER'
WHERE u.nickname LIKE 'dual_%'
ON DUPLICATE KEY UPDATE user_id = user_id;

-- USER_ROLE: SELLER para usuários dual
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'SELLER'
WHERE u.nickname LIKE 'dual_%'
ON DUPLICATE KEY UPDATE user_id = user_id;

-- FOLLOWERS - Cenários variados
-- Sellers populares têm muitos seguidores
INSERT INTO followers (user_id, seller_id)
SELECT c.id, s.id
FROM users c
CROSS JOIN users s
WHERE s.nickname IN ('seller_joao_popular', 'seller_maria_famosa', 'seller_pedro_top')
AND c.nickname IN ('customer_ana', 'customer_bruno', 'customer_carla', 'customer_diego', 'customer_elisa', 'dual_roberto', 'dual_juliana', 'dual_fernando')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Sellers iniciantes têm poucos seguidores
INSERT INTO followers (user_id, seller_id)
SELECT c.id, s.id
FROM users c
CROSS JOIN users s
WHERE s.nickname IN ('seller_lucas_iniciante', 'seller_sofia_nova')
AND c.nickname IN ('customer_ana', 'dual_roberto')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Sellers dual seguem outros sellers
INSERT INTO followers (user_id, seller_id)
SELECT c.id, s.id
FROM users c
CROSS JOIN users s
WHERE c.nickname IN ('dual_roberto', 'dual_juliana', 'dual_fernando', 'dual_patricia')
AND s.nickname IN ('seller_joao_popular', 'seller_maria_famosa', 'seller_pedro_top', 'seller_lucas_iniciante')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Customers seguem sellers dual
INSERT INTO followers (user_id, seller_id)
SELECT c.id, s.id
FROM users c
CROSS JOIN users s
WHERE c.nickname IN ('customer_bruno', 'customer_carla', 'customer_diego')
AND s.nickname IN ('dual_roberto', 'dual_juliana')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- CATEGORIES - Mais variedade
INSERT INTO categories (name) VALUES ('Cadeiras') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO categories (name) VALUES ('Teclados') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO categories (name) VALUES ('Monitores') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO categories (name) VALUES ('Mouses') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO categories (name) VALUES ('Headsets') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO categories (name) VALUES ('Webcams') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO categories (name) VALUES ('Notebooks') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO categories (name) VALUES ('Smartphones') ON DUPLICATE KEY UPDATE name = name;

-- PRODUCTS - Variados sellers e categorias
-- Seller João Popular (muitos produtos)
INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Cadeira Gamer Pro', 'Gamer', 'Racer', 'Red & Black', c.id, 1500.50, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_joao_popular' WHERE c.name = 'Cadeiras';

INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Teclado Mecânico RGB', 'Mecânico', 'HyperX', 'Black', c.id, 599.90, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_joao_popular' WHERE c.name = 'Teclados';

INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Monitor 27 4K', '4K', 'LG', 'Black', c.id, 2199.00, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_joao_popular' WHERE c.name = 'Monitores';

INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Mouse Gamer', 'Gamer', 'Logitech', 'Black', c.id, 299.90, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_joao_popular' WHERE c.name = 'Mouses';

-- Seller Maria Famosa (vários produtos)
INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Headset Wireless', 'Wireless', 'Sony', 'White', c.id, 899.00, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_maria_famosa' WHERE c.name = 'Headsets';

INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Webcam Full HD', 'Full HD', 'Logitech', 'Black', c.id, 499.90, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_maria_famosa' WHERE c.name = 'Webcams';

INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Notebook Gamer', 'Gamer', 'Acer', 'Black', c.id, 5999.00, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_maria_famosa' WHERE c.name = 'Notebooks';

-- Seller Pedro Top
INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Smartphone Pro', 'Pro', 'Samsung', 'Blue', c.id, 3499.00, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_pedro_top' WHERE c.name = 'Smartphones';

INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Cadeira Ergonômica', 'Ergonômica', 'Herman Miller', 'Gray', c.id, 3500.00, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_pedro_top' WHERE c.name = 'Cadeiras';

-- Seller Lucas Iniciante (poucos produtos)
INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Mouse Pad', 'Extended', 'Generic', 'Black', c.id, 49.90, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_lucas_iniciante' WHERE c.name = 'Mouses';

-- Seller Sofia Nova
INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Teclado Básico', 'Membrana', 'Multilaser', 'White', c.id, 89.90, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_sofia_nova' WHERE c.name = 'Teclados';

-- Sellers Dual (também vendem)
INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Monitor Ultrawide', 'Ultrawide', 'Samsung', 'Black', c.id, 2799.00, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'dual_roberto' WHERE c.name = 'Monitores';

INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Headset Gamer', 'Gamer', 'Razer', 'Green', c.id, 799.00, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'dual_juliana' WHERE c.name = 'Headsets';

INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Webcam 4K', '4K', 'Razer', 'Black', c.id, 1299.00, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'dual_fernando' WHERE c.name = 'Webcams';

-- Seller Camila Sozinha (tem produtos mas sem seguidores)
INSERT INTO products (name, type, brand, color, category_id, price, active, seller_id)
SELECT 'Smartphone Básico', 'Básico', 'Motorola', 'Black', c.id, 899.00, 1, u.id
FROM categories c JOIN users u ON u.nickname = 'seller_camila_sozinha' WHERE c.name = 'Smartphones';

-- Atualiza contadores
UPDATE users seller
SET followers_count = (
    SELECT COUNT(*) FROM followers f WHERE f.seller_id = seller.id
);

UPDATE users u
SET following_count = (
    SELECT COUNT(*) FROM followers f WHERE f.user_id = u.id
);
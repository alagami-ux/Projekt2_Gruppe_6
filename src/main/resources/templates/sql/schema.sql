CREATE DATABASE IF NOT EXISTS projekt2_gruppe_6_db;

USE projekt2_gruppe_6_db;

CREATE TABLE IF NOT EXISTS wishlist (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        title VARCHAR(150) NOT NULL,

    CONSTRAINT chk_wishlist_title
    CHECK (CHAR_LENGTH(TRIM(title)) > 0)
    );

CREATE TABLE IF NOT EXISTS wish (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    wishlist_id INT NOT NULL,
                                    name VARCHAR(150) NOT NULL,
                                    description VARCHAR(150),
                                    quantity INT NOT NULL DEFAULT 1,
                                    price DECIMAL(10, 2),
                                    product_url VARCHAR(2048),
                                    image_url VARCHAR(2048),

    CONSTRAINT fk_wish_wishlist
    FOREIGN KEY (wishlist_id)
    REFERENCES wishlist(id)
    ON DELETE CASCADE,

    CONSTRAINT chk_wish_name
    CHECK (CHAR_LENGTH(TRIM(name)) > 0),

    CONSTRAINT chk_wish_quantity
    CHECK (quantity > 0),

    CONSTRAINT chk_wish_price
    CHECK (price IS NULL OR price >= 0)
    );
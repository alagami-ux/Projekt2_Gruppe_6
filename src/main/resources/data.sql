USE projekt2_gruppe_6_db;

START TRANSACTION;

INSERT INTO wishlist (title)
VALUES ('Fødselsdag');

SET @wishlist_id = LAST_INSERT_ID();

INSERT INTO wish (
    wishlist_id, name, description,
    quantity, price, product_url, image_url
)
VALUES
    (@wishlist_id, 'Bog', 'En gyser ',
     1, 199.95, NULL, NULL),

    (@wishlist_id, 'Kaffekop', 'En blå kop',
     2, 79.50, NULL, NULL),

    (@wishlist_id, 'PS5', 'Til gaming',
     1, 299.00, NULL, NULL);

COMMIT;
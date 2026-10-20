USE projekt2_gruppe_6_db;

-- Run once on a new database. Existing data is preserved.
START TRANSACTION;
INSERT INTO wishlist (title) VALUES ('Fødselsdag');
SET @birthday_list_id = LAST_INSERT_ID();
INSERT INTO wish (wishlist_id, name, description, quantity, price, product_url, image_url)
VALUES (@birthday_list_id, 'Bog', 'En god roman', 1, 199.95, NULL, NULL),
       (@birthday_list_id, 'Kaffekop', 'En blå kop', 2, 79.50, NULL, NULL);
INSERT INTO wishlist (title) VALUES ('Jul');
COMMIT;

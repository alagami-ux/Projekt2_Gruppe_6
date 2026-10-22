USE projekt2_gruppe_6_db;

START TRANSACTION;

INSERT INTO wishlist (title) VALUES ('Fødselsdag');
SET @birthday_id = LAST_INSERT_ID();

INSERT INTO wishlist (title) VALUES ('Juleønsker');
SET @christmas_id = LAST_INSERT_ID();

INSERT INTO wish (
    wishlist_id, name, description,
    quantity, price, product_url, image_url
)
VALUES
    (@birthday_id, 'Bog', 'En uhyggelig gyser',
     1, 199.95, NULL, NULL),

    (@christmas_id, 'Kaffekop', 'En blå stor kop',
     2, 79.50, 'https://www.bilka.dk', 'blåkop.png'),

    (@birthday_id, 'PS5', 'Til at spille de nyeste spil.',
     1, 5000, 'https://www.elgiganten.dk', 'PS5.png'),

    (@christmas_id, 'Fodbold', 'Den nyeste fodbold',
     1, 399.95, NULL, NULL),

    (@birthday_id, 'GTA6', 'Den nyeste GTA6, som kan spilles på PS5',
     1, 700, 'https://www.elgiganten.dk/brand/rockstar/gta-6-release', NULL);

COMMIT;

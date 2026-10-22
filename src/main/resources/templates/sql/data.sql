USE projekt2_gruppe_6_db;

INSERT INTO wishlist (title)
VALUES ('Fødselsdag'),
       ('Juleønsker');

INSERT INTO wish (
    wishlist_id, name, description,
    quantity, price, product_url, image_url
)
VALUES
    (1, 'Bog', 'En uhyggelig gyser',
     1, 199.95, NULL, NULL),

    (2, 'Kaffekop', 'En blå stor kop',
     2, 79.50, 'https://www.bilka.dk', 'blåkop.png'),

    (1, 'PS5', 'Til at spille de nyeste spil.',
     1, 5000, 'https://www.elgiganten.dk', 'PS5.png'),

    (2, 'Fodbold', 'Den nyeste fodbold',
     1, 399.95, NULL, NULL),

    (1, 'GTA6', 'Den nyeste GTA6, som kan spilles på PS5',
     1, 700, 'https://www.elgiganten.dk/brand/rockstar/gta-6-release', NULL);

    COMMIT;
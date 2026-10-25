USE projekt2_gruppe_6_db;

START TRANSACTION;

INSERT INTO wishlist (title) VALUES ('Fødselsdag');
SET @birthday_id = LAST_INSERT_ID();

INSERT INTO wishlist (title) VALUES ('Juleønsker');
SET @christmas_id = LAST_INSERT_ID();

-- Priser kontrolleret 25-10-2026, uden fragt.
-- Priser og lagerstatus opdateres ikke automatisk.

INSERT INTO wish (
    wishlist_id,
    name,
    description,
    quantity,
    price,
    product_url,
    image_url
)
VALUES
    (
        @birthday_id,
        'Bog',
        'Weaveworld af Clive Barker',
        1,
        189.95,
        'https://www.bog-ide.dk/products/weaveworld-clive-barker-paperback-1772434',
        'https://www.bog-ide.dk/cdn/shop/files/9780006483007.jpg?width=600'
    ),
    (
        @christmas_id,
        'Kaffekop',
        'Et hvidt og blåt krus med teksten Oldefar',
        2,
        69.95,
        'https://www.bog-ide.dk/products/krus-enjoy-oldefar-hvid-og-blaa-2647340',
        'https://www.bog-ide.dk/cdn/shop/files/2647340_ftp.jpg?width=600'
    ),
    (
        @birthday_id,
        'PS5 Slim Digital Edition',
        '825 GB, E-chassis. Konsol uden diskdrev.',
        1,
        5299.00,
        'https://www.elgiganten.dk/product/gaming/spillekonsol-tilbehor/playstation/ps5-playstation-5-spillekonsol/playstation-5-slim-digital-edition-e-chassis-825gb/988058',
        'https://www.elgiganten.dk/image/dv_web_D18000134615294/988058/playstation-5-slim-digital-edition-e-chassis-825gb.jpg'
    ),
    (
        @christmas_id,
        'Fodbold',
        'En orange fodbold i størrelse 4',
        1,
        199.95,
        'https://www.legekaeden.dk/products/fodbold-monkey-orange-str-4-3484110',
        'https://www.legekaeden.dk/cdn/shop/files/3484110_ftp.jpg?v=1786429817&width=2048'
    ),
    (
        @birthday_id,
        'GTA6 til PS5',
        'Forudbestilling. Fysisk æske med downloadkode, ingen disk.',
        1,
        599.00,
        'https://www.elgiganten.dk/product/gaming/spil/gta-6-grand-theft-auto-vi-ps5/1119038',
        'https://www.elgiganten.dk/image/dv_web_D180001418491/1119038/gta-6-grand-theft-auto-vi-ps5.jpg'
    );

COMMIT;
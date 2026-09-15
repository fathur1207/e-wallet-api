INSERT INTO users (id, name, email, balance)
SELECT * FROM (SELECT 1 AS id, 'Andi Wijaya' AS name, 'andi@example.com' AS email, 1000000.00 AS balance) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 1);

INSERT INTO users (id, name, email, balance)
SELECT * FROM (SELECT 2 AS id, 'Budi Santoso' AS name, 'budi@example.com' AS email, 500000.00 AS balance) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 2);

INSERT INTO users (id, name, email, balance)
SELECT * FROM (SELECT 3 AS id, 'Citra Lestari' AS name, 'citra@example.com' AS email, 750000.00 AS balance) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 3);

-- Datos de prueba compartidos entre H2 y MySQL
-- Este archivo es la ÚNICA FUENTE DE VERDAD para los datos de test

-- Bancos
INSERT INTO banks(name, total_transfers)
VALUES('BCP', 0),
('BBVA', 0),
('Interbank', 0),
('Scotiabank', 0);

-- Cuentas
INSERT INTO accounts(holder, balance, bank_id)
VALUES('Lesly Águila', 3000.00, 1),
('Cielo Fernández', 2000.00, 1),
('Susana Alvarado', 5000.00, 2),
('Briela Cirilo', 1000.00, 2),
('Milagros Díaz', 3500.00, 3),
('Kiara Lozano', 100.00, 4),
('Analucía Urbina', 4000.00, 4),
('Yrma Guerrero', 7000.00, 4);

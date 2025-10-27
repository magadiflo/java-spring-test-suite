SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE accounts;
TRUNCATE TABLE banks;

SET FOREIGN_KEY_CHECKS = 1;

-- Bancos
INSERT INTO banks(name, total_transfers)
VALUES('Banco Continental', 0),
('Banco de Crédito', 0),
('Interbank', 0);

-- Cuentas
INSERT INTO accounts(holder, balance, bank_id)
VALUES('Juan Pérez', 5000.00, 1),
('María García', 3000.00, 1),
('Carlos López', 7500.00, 2),
('Ana Martínez', 2000.00, 3);

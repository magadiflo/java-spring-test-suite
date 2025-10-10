-- ============================================
-- LIMPIAR DATOS EXISTENTES Y RESETEAR IDS
-- ============================================

-- Desactivar verificación de llaves foráneas temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- Limpiar tablas (TRUNCATE resetea AUTO_INCREMENT automáticamente)
TRUNCATE TABLE accounts;
TRUNCATE TABLE banks;

-- Reactivar verificación de llaves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- INSERTAR DATOS INICIALES
-- ============================================

-- Bancos
INSERT INTO banks(name, total_transfers) VALUES('Banco Continental', 0);
INSERT INTO banks(name, total_transfers) VALUES('Banco de Crédito', 0);
INSERT INTO banks(name, total_transfers) VALUES('Interbank', 0);

-- Cuentas
INSERT INTO accounts(holder, balance, bank_id) VALUES('Juan Pérez', 5000.00, 1);
INSERT INTO accounts(holder, balance, bank_id) VALUES('María García', 3000.00, 1);
INSERT INTO accounts(holder, balance, bank_id) VALUES('Carlos López', 7500.00, 2);
INSERT INTO accounts(holder, balance, bank_id) VALUES('Ana Martínez', 2000.00, 3);
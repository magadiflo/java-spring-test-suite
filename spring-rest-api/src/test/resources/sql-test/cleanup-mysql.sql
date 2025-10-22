-- Limpieza de datos para MySQL
-- Desactiva temporalmente las validaciones de foreign keys
SET FOREIGN_KEY_CHECKS = 0;

-- Limpia las tablas en cualquier orden
TRUNCATE TABLE accounts;
TRUNCATE TABLE banks;

-- Reactivar verificación de foreign keys
SET FOREIGN_KEY_CHECKS = 1;

-- Limpieza de datos para H2
-- Desactiva temporalmente las validaciones de integridad referencial
SET REFERENTIAL_INTEGRITY FALSE;

-- Limpia las tablas en cualquier orden (sin preocuparte por FKs)
TRUNCATE TABLE accounts;
TRUNCATE TABLE banks;

-- Reactiva las validaciones
SET REFERENTIAL_INTEGRITY TRUE;

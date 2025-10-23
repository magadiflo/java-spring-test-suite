-- Reiniciar IDs y limpiar tablas existentes
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE accounts;
TRUNCATE TABLE banks;

SET FOREIGN_KEY_CHECKS = 1;

-- Insertar datos de ejemplo
INSERT INTO customers(name, email)
VALUES('María Briones', 'maria.briones@gmail.com'),
('Karito Casanova', 'karito.casanova@gmail.com'),
('Luis Castillo', 'luis.castillo@gmail.com'),
('Diego Campomanes', 'diego.campomanes@gmail.com'),
('Alexander Villanueva', 'alexander.villanueva@gmail.com');

-- ==============================================================================
-- EMBROIDERYDASH ERP: INICIALIZACIÓN DE BASES DE DATOS AISLADAS
-- ==============================================================================
-- Este script se ejecuta automáticamente en el primer arranque del contenedor PostgreSQL.
-- Cada microservicio dispone de su propia base de datos y usuario dedicado,
-- garantizando la regla arquitectónica de aislamiento de datos sin acceso cruzado.

-- 1. Base de datos y usuario: servicio-disenos
CREATE USER disenos_user WITH PASSWORD 'disenos_pass_dev_secret';
CREATE DATABASE embroidery_disenos OWNER disenos_user;
GRANT ALL PRIVILEGES ON DATABASE embroidery_disenos TO disenos_user;

-- 2. Base de datos y usuario: servicio-inventario
CREATE USER inventario_user WITH PASSWORD 'inventario_pass_dev_secret';
CREATE DATABASE embroidery_inventario OWNER inventario_user;
GRANT ALL PRIVILEGES ON DATABASE embroidery_inventario TO inventario_user;

-- 3. Base de datos y usuario: servicio-trabajos
CREATE USER trabajos_user WITH PASSWORD 'trabajos_pass_dev_secret';
CREATE DATABASE embroidery_trabajos OWNER trabajos_user;
GRANT ALL PRIVILEGES ON DATABASE embroidery_trabajos TO trabajos_user;

-- 4. Base de datos y usuario: servicio-pagos
CREATE USER pagos_user WITH PASSWORD 'pagos_pass_dev_secret';
CREATE DATABASE embroidery_pagos OWNER pagos_user;
GRANT ALL PRIVILEGES ON DATABASE embroidery_pagos TO pagos_user;

-- 5. Base de datos y usuario: identidad/autenticación
CREATE USER auth_user WITH PASSWORD 'auth_pass_dev_secret';
CREATE DATABASE embroidery_auth OWNER auth_user;
GRANT ALL PRIVILEGES ON DATABASE embroidery_auth TO auth_user;

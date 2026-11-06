ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_role_check;
ALTER TABLE usuarios ADD CONSTRAINT usuarios_role_check
    CHECK (role IN ('ADMIN', 'ADMIN_EMPRESA', 'DISENADOR', 'OPERADOR'));

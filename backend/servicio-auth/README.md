# Servicio de autenticación e identidad

Emite los JWT que consumen los microservicios. Roles internos:

- `ADMIN`: administrador inicial, permisos administrativos y acceso a gestión de usuarios.
- `OPERADOR`: usuario operativo (presentado en frontend como “Usuario”). No puede listar ni crear usuarios.

## Endpoints

- `POST /api/v1/auth/login` — público. Body `{ "username": "admin", "password": "..." }`.
- `GET /api/v1/auth/me` — perfil de quien presenta Bearer JWT.
- `GET /api/v1/usuarios` — solo ADMIN.
- `POST /api/v1/usuarios` — solo ADMIN. Body `{ "username": "...", "displayName": "...", "password": "mínimo 12 caracteres" }`. Crea siempre un `OPERADOR`.

La contraseña se almacena con BCrypt (factor 12); no hay endpoint que devuelva hashes, cambie privilegios o promueva cuentas. Los tokens expiran según `JWT_EXPIRATION_MS`. Debe configurarse exactamente el mismo `JWT_SECRET` en cada microservicio.

Al iniciar, se crea una sola vez el administrador de `ADMIN_USERNAME`, `ADMIN_DISPLAY_NAME` y `ADMIN_PASSWORD`. Si esa cuenta ya existe, las variables no modifican su contraseña. El servicio falla al iniciar si la contraseña inicial no está configurada, es menor a 12 caracteres o excede el límite de 72 bytes de BCrypt.

Para ejecutar solo identidad localmente: crea la base PostgreSQL `embroidery_auth`, configura las variables anteriores (`JWT_SECRET` debe ser una clave aleatoria de al menos 32 bytes; puedes generarla con `openssl rand -base64 32`) y ejecuta `./mvnw spring-boot:run -f backend/servicio-auth/pom.xml` (Windows: `mvnw.cmd`). Puerto 8100.

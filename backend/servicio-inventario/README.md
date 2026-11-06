# Servicio de Inventario (servicio-inventario)

Microservicio encargado del control de stock de hilos, existencias de conos, trazabilidad de movimientos de almacén (entradas, salidas, ajustes) y alertas de inventario crítico.

## Especificaciones Técnicas

- **Lenguaje:** Java 21 LTS
- **Framework:** Spring Boot 3.3.5
- **Persistencia:** Spring Data JPA / Hibernate con **Bloqueo Pesimista (`PESSIMISTIC_WRITE`)**
- **Base de Datos:** PostgreSQL (`embroidery_inventario`)
- **Migraciones:** Flyway (`V1` esquema, `V2` datos de prueba)
- **Seguridad:** Spring Security con Tokens JWT
- **Documentación OpenAPI:** Swagger UI en `/swagger-ui.html`
- **Puerto:** `8097`

## Endpoints Principales

| Método | Endpoint | Roles Permitidos | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/inventario/hilos` | `ADMIN`, `OPERADOR` | Listar catálogo de hilos y existencias |
| `GET` | `/api/v1/inventario/hilos/{id}` | `ADMIN`, `OPERADOR` | Consultar hilo por ID |
| `GET` | `/api/v1/inventario/hilos/referencia/{ref}` | `ADMIN`, `OPERADOR` | Consultar hilo por referencia comercial |
| `GET` | `/api/v1/inventario/hilos/alertas` | `ADMIN`, `OPERADOR` | Listar hilos con stock crítico (bajo mínimo) |
| `POST` | `/api/v1/inventario/hilos` | `ADMIN` | Registrar nuevo hilo en catálogo |
| `PUT` | `/api/v1/inventario/hilos/{id}` | `ADMIN` | Actualizar propiedades del hilo |
| `DELETE` | `/api/v1/inventario/hilos/{id}` | `ADMIN` | Baja lógica de hilo |
| `POST` | `/api/v1/inventario/movimientos` | `ADMIN`, `OPERADOR` | Registrar entrada, salida o ajuste de conos |
| `GET` | `/api/v1/inventario/movimientos/hilo/{hiloId}` | `ADMIN`, `OPERADOR` | Historial de movimientos de un hilo |
| `GET` | `/api/v1/inventario/movimientos` | `ADMIN`, `OPERADOR` | Historial general paginado |

## Pruebas y Verificación

```bash
# Ejecutar test suite
.\mvnw.cmd test -f backend/servicio-inventario/pom.xml
```

## Fotos y c�digos de color

El c�digo de color del hilo debe contener entre 1 y 20 d�gitos. Las fotos JPG/PNG se aceptan hasta 2 MB y se decodifican, reducen a un m�ximo de 600 px y recodifican como PNG. Compose monta las im�genes en el volumen `inventario_storage`.

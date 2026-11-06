# Servicio de Pagos y Cobranzas

Microservicio Spring Boot 3.3.5 / Java 21 para registrar pagos parciales, consultar historial y resumen por trabajo, y anular pagos con trazabilidad.

## Endpoints

| MÃ©todo | Ruta | Permiso | DescripciÃ³n |
|---|---|---|---|
| `POST` | `/api/v1/pagos` | ADMIN, OPERADOR | Registra un pago. Requiere `Idempotency-Key` (hasta 100 caracteres). |
| `GET` | `/api/v1/pagos/trabajo/{trabajoId}` | ADMIN, OPERADOR | Historial, mÃ¡s reciente primero. |
| `GET` | `/api/v1/pagos/trabajo/{trabajoId}/resumen` | ADMIN, OPERADOR | Total pagado, saldo y estado derivado. |
| `POST` | `/api/v1/pagos/{id}/anular` | ADMIN | Anula sin borrar; deja de sumar al total. |

Ejemplo:

```http
POST /api/v1/pagos
Authorization: Bearer <JWT>
Idempotency-Key: cobro-orden-3-adelanto
Content-Type: application/json

{"trabajoId":3,"monto":100.00,"metodoPago":"EFECTIVO"}
```

El JWT debe usar el mismo `JWT_SECRET` que `servicio-auth` y `servicio-trabajos`. Repetir una clave idempotente con los mismos trabajo, monto y medio de pago devuelve el pago original; usarla con datos distintos produce HTTP 409. Se adquieren bloqueos PostgreSQL por trabajo para evitar sobrecobros concurrentes.

## Desarrollo y pruebas

```bash
./mvnw test -f backend/servicio-pagos/pom.xml
./mvnw spring-boot:run -f backend/servicio-pagos/pom.xml
```

Puerto `8099`; URL de trabajos configurable mediante `CLIENT_TRABAJOS_URL`.

## Tickets de bordado

Los tickets guardan el nombre y precio unitario del diseño en el momento de creación. Los pagos nuevos pueden asociarse a un ticket mediante `ticketId`, conservan `Idempotency-Key` y no pueden superar el saldo; al completar el importe el estado pasa a PAGADO. Los tickets se anulan solo sin pagos vigentes.

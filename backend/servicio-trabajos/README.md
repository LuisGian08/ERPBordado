# Servicio de Trabajos y Costos (servicio-trabajos)

Microservicio encargado del ciclo de vida de órdenes de bordado, estados de producción, cálculo paramétrico de costos estimados, liquidación de costos reales y márgenes comerciales.

## Especificaciones Técnicas

- **Lenguaje:** Java 21 LTS
- **Framework:** Spring Boot 3.3.5
- **Persistencia:** Spring Data JPA / Hibernate con `BigDecimal` para cálculos financieros
- **Base de Datos:** PostgreSQL (`embroidery_trabajos`)
- **Migraciones:** Flyway (`V1` esquema, `V2` datos de prueba)
- **Comunicación REST:** Spring 6 `RestClient` para consumo de `servicio-disenos` y `servicio-inventario`
- **Seguridad:** Spring Security con Tokens JWT
- **Documentación OpenAPI:** Swagger UI en `/swagger-ui.html`
- **Puerto:** `8098`

## Fórmula de Costos Paramétrica

Configurada en `application.yml` bajo `app.costos`:

$$\text{Costo Estimado} = \text{Costo Montaje Base} + \left(\frac{\text{Puntadas Totales}}{1000} \times \text{Costo por Mil Puntadas}\right) + \left(\frac{\text{Puntadas Totales}}{\text{Velocidad SPM}} \times \text{Costo Minuto Operador}\right) + (\text{Costo Entretela} \times \text{Prendas})$$

- $\text{Margen Estimado} = \text{Precio Venta} - \text{Costo Estimado}$
- $\text{Porcentaje Margen} = \left(\frac{\text{Margen Estimado}}{\text{Precio Venta}}\right) \times 100$

## Endpoints Principales

| Método | Endpoint | Roles Permitidos | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/trabajos/calcular-costo` | `ADMIN`, `OPERADOR` | Simular cálculo analítico de costos |
| `GET` | `/api/v1/trabajos` | `ADMIN`, `OPERADOR` | Listar órdenes paginadas con filtro por cliente |
| `GET` | `/api/v1/trabajos/{id}` | `ADMIN`, `OPERADOR` | Consultar orden por ID |
| `GET` | `/api/v1/trabajos/codigo/{codigo}` | `ADMIN`, `OPERADOR` | Consultar orden por código público |
| `POST` | `/api/v1/trabajos` | `ADMIN`, `OPERADOR` | Crear nueva orden de bordado |
| `PATCH` | `/api/v1/trabajos/{id}/estado` | `ADMIN`, `OPERADOR` | Transición de estado de producción |
| `POST` | `/api/v1/trabajos/{id}/liquidar-costo` | `ADMIN` | Asentar costo real medido tras producción |

## Pruebas y Verificación

```bash
# Ejecutar test suite
.\mvnw.cmd test -f backend/servicio-trabajos/pom.xml
```

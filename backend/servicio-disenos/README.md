# Servicio de Diseños (servicio-disenos)

Microservicio encargado de la administración del catálogo de diseños digitales de bordado, registro de puntadas, secuencias de colores de hilo y archivos técnicos (DST, PES).

## Especificaciones Técnicas

- **Lenguaje:** Java 21 LTS
- **Framework:** Spring Boot 3.3.5
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** PostgreSQL (`embroidery_disenos`)
- **Migraciones:** Flyway (`V1` esquema, `V2` datos de muestra)
- **Seguridad:** Spring Security con validación de Tokens JWT
- **Documentación OpenAPI:** Swagger UI en `/swagger-ui.html`
- **Puerto:** `8096`

## Endpoints Principales

| Método | Endpoint | Roles Permitidos | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/disenos` | `ADMIN`, `OPERADOR` | Listar todos los diseños activos |
| `GET` | `/api/v1/disenos/{id}` | `ADMIN`, `OPERADOR` | Obtener detalle de diseño con colores y archivos |
| `POST` | `/api/v1/disenos` | `ADMIN` | Crear nuevo diseño con validación de unicidad |
| `PUT` | `/api/v1/disenos/{id}` | `ADMIN` | Actualizar diseño existente |
| `DELETE` | `/api/v1/disenos/{id}` | `ADMIN` | Baja lógica de diseño |

## Ejecución Local y Pruebas

```bash
# Ejecutar pruebas unitarias e integración
.\mvnw.cmd test -f backend/servicio-disenos/pom.xml

# Ejecutar el servicio en modo local
.\mvnw.cmd spring-boot:run -f backend/servicio-disenos/pom.xml
```

## Analizador de archivos de bordado

El analizador privado `servicio-preview` se integra por Docker Compose en el puerto interno 8101. Configura `PREVIEW_INTERNAL_TOKEN` en `.env` (32 bytes aleatorios; `openssl rand -hex 32`). Para publicar el servicio solo durante desarrollo, usa `compose.preview-dev.yaml`. Los detalles del endpoint y m�tricas se encuentran en `backend/servicio-preview/README.md`.

## Métricas, almacenamiento y diagnósticos

Una **puntada** suma exclusivamente un comando `STITCH`. `JUMP`, `TRIM`, `COLOR_CHANGE` y otros comandos de control no incrementan el total. En DST, la vista previa usa una paleta predeterminada porque el archivo no almacena los colores reales del hilo.

El contenedor repara al arrancar la propiedad del volumen `disenos_storage` y ejecuta la JVM como `appuser`; esto también cubre volúmenes nombrados que se crearon previamente como root.

El error de renombrado de una carpeta raíz ocurría al leer `carpetaPadreId=null`: `Stream.findFirst()` lanza `NullPointerException` cuando el valor emitido es nulo. La lectura ahora usa un bucle que admite ese valor. No pude obtener la traza del fallo de análisis DST en este entorno porque Docker no está disponible; por tanto, los permisos del volumen eran una causa candidata, no una causa raíz confirmada. El entrypoint nuevo corrige ese caso y los errores multipart se devuelven como 4xx con código y mensaje claros.

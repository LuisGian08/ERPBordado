# EmbroideryDash ERP frontend

Next.js App Router, React y TypeScript. Ejecuta `npm install`, copia `.env.local.example` a `.env.local` y luego `npm run dev`.

## Implementado

- Layout responsive, inicio/cierre de sesion JWT y administracion de usuarios visible para ADMIN.
- Dashboard con estados vacios explicitos, sin cifras inventadas.
- Catalogo de disenos con busqueda, detalle, alta, edicion, baja logica y secuencia de colores. Usa `GET/POST /api/v1/disenos` y `GET/PUT/DELETE /api/v1/disenos/{id}`.
- Puntadas y dimensiones X (ancho) e Y (alto) en milimetros.
- Capa HTTP central que adjunta el token Bearer en memoria del navegador.

## Limitaciones pendientes

- El servicio de disenos no implementa carga/descarga DST/PES ni asociacion o generacion de imagenes de vista previa.
- No hay todavia pantallas operativas para inventario, trabajos, costos, pagos ni reportes. El dashboard/reportes agregados tampoco tienen endpoint.

## Usuarios y autenticacion

- `POST /api/v1/auth/login` acepta `{ "username": "...", "password": "..." }` y devuelve JWT Bearer, expiracion y perfil.
- `GET /api/v1/auth/me` devuelve el perfil de quien presenta el token.
- `GET/POST /api/v1/usuarios` requieren ADMIN. La creacion siempre da rol `OPERADOR` (presentado como Usuario).
- Configura el mismo `JWT_SECRET` en todos los servicios y un `ADMIN_PASSWORD` de 12 a 72 caracteres.
- Directo (modo actual): URLs de `NEXT_PUBLIC_*_API_URL` apuntan a puertos 8096-8100.
- Via Gateway: configura `NEXT_PUBLIC_AUTH_API_URL`, `NEXT_PUBLIC_DISENOS_API_URL`, `NEXT_PUBLIC_INVENTARIO_API_URL`, `NEXT_PUBLIC_TRABAJOS_API_URL` y `NEXT_PUBLIC_PAGOS_API_URL` en `http://localhost:8095`.

## API de pagos

- `POST /api/v1/pagos` registra con header `Idempotency-Key` obligatorio.
- `GET /api/v1/pagos/trabajo/{id}` lista historial; `GET /api/v1/pagos/trabajo/{id}/resumen` calcula saldo y estado.
- `POST /api/v1/pagos/{id}/anular` requiere ADMIN.

## Funciones de catálogo, inventario y cobranza

- El catálogo usa `/api/v1/carpetas/contenido` y el asistente analiza DST/PES mediante `/api/v1/disenos/archivos/analizar`. El servicio privado `servicio-preview` escucha en el puerto 8101 interno y exige `PREVIEW_INTERNAL_TOKEN`; no publica un puerto host en `compose.yaml`. Para depuración local se puede publicar con `docker compose -f compose.yaml -f compose.preview-dev.yaml up`.
- Las vistas previas, miniaturas y fotos se cargan mediante `AuthImage`, que usa `fetch` con Bearer JWT.
- Costos obtiene precios desde `/api/v1/disenos/{id}/precio`; solo ADMIN puede guardarlos. Los cambios quedan en el historial en PEN.
- Pagos usa los tickets `/api/v1/tickets` y `/api/v1/pagos` con `ticketId` e `Idempotency-Key`.
- Las imágenes del inventario se guardan como PNG recodificado bajo el volumen `inventario_storage`; el código de color acepta de 1 a 20 dígitos.

Las URLs `NEXT_PUBLIC_*_API_URL` deben apuntar al gateway `http://localhost:8095`.

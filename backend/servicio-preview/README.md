# servicio-preview

Servicio privado que analiza archivos DST/PES/EXP y devuelve sus métricas y PNG de vista previa. Escucha en `8101` dentro de la red Docker; no se publica al host ni se enruta por el gateway. Cada petición, salvo `/health`, exige `X-Internal-Token`, comparado en tiempo constante con `PREVIEW_INTERNAL_TOKEN`.

`POST /api/v1/preview/analizar` acepta el multipart `archivo`, hasta `MAX_FILE_MB` (25 MB por defecto). Calcula las puntadas contando únicamente comandos STITCH; las dimensiones convierten las unidades de décimas de milímetro de los formatos a milímetros; para colores usa la lista de hilos del archivo y, cuando no existe, el número de cambios de color más el hilo inicial. Jumps y trims no se dibujan. Las imágenes PNG son transparentes, con proporción conservada y lado máximo de 1000 px (200 px para miniatura).

Una **puntada** en las métricas es exclusivamente un comando `STITCH` del archivo. Los comandos `JUMP`, `TRIM`, `COLOR_CHANGE` y otros comandos de control no se suman al total. En DST no se almacenan los colores reales del hilo; la vista previa representa los cambios con una paleta por defecto y el número de colores se calcula como cambios de color más el hilo inicial cuando el archivo no trae una lista de hilos.

El servicio limita los diseños a 1.500.000 puntadas, el lienzo a un millón de píxeles y el render a 10 segundos. En Compose, `PREVIEW_INTERNAL_TOKEN` debe configurarse en `.env`; genera uno con `openssl rand -hex 32`. Para ejecutar las pruebas: `pytest`.

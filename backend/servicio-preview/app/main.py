import hmac
import io
import logging
import os
import tempfile
import time
from pathlib import Path

import pyembroidery
from fastapi import FastAPI, File, Header, HTTPException, Request, UploadFile
from fastapi.exception_handlers import request_validation_exception_handler
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from PIL import Image, ImageDraw

MAX_FILE_MB = min(25, max(1, int(os.getenv("MAX_FILE_MB", "25"))))
MAX_STITCHES = 1_500_000
MAX_SIDE = 1000
PALETTE = ["#e4572e", "#17a398", "#ffc914", "#4b3f72", "#2e86ab", "#d7263d", "#6a994e"]
COMMANDS = {
    "DST": pyembroidery.read_dst,
    "PES": pyembroidery.read_pes,
    "EXP": pyembroidery.read_exp,
}

app = FastAPI(title="EmbroideryDash Preview", docs_url=None, redoc_url=None)
logger = logging.getLogger(__name__)


@app.on_event("startup")
async def require_internal_token():
    if not os.getenv("PREVIEW_INTERNAL_TOKEN"):
        raise RuntimeError("PREVIEW_INTERNAL_TOKEN es obligatorio")


@app.exception_handler(HTTPException)
async def json_http_error(_, exc: HTTPException):
    messages = {401: "Token interno inválido", 413: "El archivo supera el tamaño máximo", 415: "Formato de bordado no compatible", 422: "El archivo está dañado o no contiene puntadas válidas"}
    return JSONResponse(status_code=exc.status_code, content={"code": {401: "TOKEN_INTERNO_INVALIDO", 413: "ARCHIVO_DEMASIADO_GRANDE", 415: "FORMATO_NO_SOPORTADO", 422: "ARCHIVO_INVALIDO"}.get(exc.status_code, "ERROR"), "message": messages.get(exc.status_code, str(exc.detail))})


@app.exception_handler(RequestValidationError)
async def log_request_validation_error(request: Request, exc: RequestValidationError):
    logger.warning(
        "Solicitud rechazada por validación FastAPI: path=%s content_type=%s errors=%s",
        request.url.path,
        request.headers.get("content-type", ""),
        exc.errors(),
    )
    return await request_validation_exception_handler(request, exc)


@app.get("/health")
async def health():
    return {"status": "ok"}


def _colors(pattern):
    threadlist = getattr(pattern, "threadlist", None) or []
    result = []
    for thread in threadlist:
        color = getattr(thread, "color", None)
        if isinstance(color, int):
            result.append(f"#{color & 0xFFFFFF:06X}")
    return result


def _analyze(path: Path, extension: str):
    pattern = COMMANDS[extension](str(path))
    stitches = list(pattern.stitches)
    count = sum(1 for _, _, command in stitches if command == pyembroidery.STITCH)
    if count == 0:
        raise ValueError("sin puntadas")
    if count > MAX_STITCHES:
        raise OverflowError("demasiadas puntadas")
    bounds = pattern.bounds()
    left, top, right, bottom = bounds
    width_units, height_units = max(0, right - left), max(0, bottom - top)
    width_mm, height_mm = width_units / 10, height_units / 10
    colors = _colors(pattern)
    color_changes = sum(1 for _, _, command in stitches if command == pyembroidery.COLOR_CHANGE)
    if not colors:
        colors = [PALETTE[index % len(PALETTE)] for index in range(color_changes + 1)]
    colors = [color.upper() for color in colors]
    # The machine command stream is rendered in order; jumps and trims only move the needle.
    geometry = [(x, y, command) for x, y, command in stitches]
    max_extent = max(width_units, height_units, 1)
    # Render twice the target resolution, then downsample with Lanczos.
    side = min(MAX_SIDE * 2, max(256, int(min(1600, max_extent / 10 * 8))))
    margin = max(16, side // 20)
    scale = (side - 2 * margin) / max_extent
    out_w = max(32, int(width_units * scale) + margin * 2)
    out_h = max(32, int(height_units * scale) + margin * 2)
    if out_w * out_h > 4_000_000:
        raise ValueError("imagen demasiado grande")
    started = time.monotonic()
    image = Image.new("RGBA", (out_w, out_h), (255, 255, 255, 0))
    draw = ImageDraw.Draw(image)
    prior = None
    color_index = 0
    draw_count = 0
    pen = max(1, round(side / 500))
    for x, y, command in geometry:
        if time.monotonic() - started > 10:
            raise TimeoutError("límite de render excedido")
        if command == pyembroidery.COLOR_CHANGE:
            color_index += 1
            prior = None
            continue
        point = (margin + round((x - left) * scale), margin + round((y - top) * scale))
        if command == pyembroidery.STITCH:
            if prior is not None:
                draw.line((prior, point), fill=colors[color_index % len(colors)], width=pen)
            prior = point
            draw_count += 1
        elif command in (pyembroidery.JUMP, pyembroidery.TRIM):
            prior = None
    def encode(max_side):
        thumb = image.copy()
        thumb.thumbnail((max_side, max_side), Image.Resampling.LANCZOS)
        stream = io.BytesIO()
        thumb.save(stream, format="PNG", optimize=True)
        import base64
        return base64.b64encode(stream.getvalue()).decode("ascii")
    return {"formato": extension, "puntadas": count, "anchoMm": round(width_mm, 2), "altoMm": round(height_mm, 2), "cantidadColores": len(colors), "cambiosColor": color_changes, "colores": colors, "previewPngBase64": encode(MAX_SIDE), "miniaturaPngBase64": encode(200)}


@app.post("/api/v1/preview/analizar")
async def analizar(archivo: UploadFile = File(...), x_internal_token: str | None = Header(default=None, alias="X-Internal-Token")):
    expected = os.getenv("PREVIEW_INTERNAL_TOKEN", "")
    if not x_internal_token or not hmac.compare_digest(x_internal_token.encode(), expected.encode()):
        raise HTTPException(status_code=401)
    extension = Path(archivo.filename or "").suffix.lower().lstrip(".").upper()
    if extension not in COMMANDS:
        raise HTTPException(status_code=415)
    payload = await archivo.read(MAX_FILE_MB * 1024 * 1024 + 1)
    logger.info("Solicitud de análisis de bordado: formato=%s bytes=%d", extension, len(payload))
    if not payload:
        logger.warning("Archivo de bordado vacío: formato=%s", extension)
        raise HTTPException(status_code=422)
    if len(payload) > MAX_FILE_MB * 1024 * 1024:
        raise HTTPException(status_code=413)
    try:
        with tempfile.NamedTemporaryFile(prefix="preview-", suffix=f".{extension.lower()}", delete=True) as temp:
            temp.write(payload)
            temp.flush()
            result = _analyze(Path(temp.name), extension)
    except OverflowError:
        logger.warning("Archivo de bordado excede el máximo de puntadas: formato=%s bytes=%d", extension, len(payload), exc_info=True)
        raise HTTPException(status_code=422)
    except TimeoutError:
        logger.warning("Se agotó el tiempo al renderizar el bordado: formato=%s bytes=%d", extension, len(payload), exc_info=True)
        raise HTTPException(status_code=422)
    except Exception:
        logger.exception("No se pudo analizar el archivo de bordado: formato=%s bytes=%d", extension, len(payload))
        raise HTTPException(status_code=422)
    return result

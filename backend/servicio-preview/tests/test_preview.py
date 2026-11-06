import base64
import os
import tempfile
from pathlib import Path

import pyembroidery
import pytest
from fastapi.testclient import TestClient

os.environ["PREVIEW_INTERNAL_TOKEN"] = "test-internal-token"
from app.main import app

client = TestClient(app)
HEADERS = {"X-Internal-Token": "test-internal-token"}


def stitch_file(writer, suffix):
    pattern = pyembroidery.EmbPattern()
    pattern.add_stitch_absolute(pyembroidery.STITCH, 0, 0)
    pattern.add_stitch_absolute(pyembroidery.STITCH, 1200, 0)
    pattern.add_stitch_absolute(pyembroidery.STITCH, 1200, 900)
    pattern.add_command(pyembroidery.COLOR_CHANGE)
    pattern.add_stitch_absolute(pyembroidery.STITCH, 0, 900)
    pattern.add_stitch_absolute(pyembroidery.STITCH, 0, 0)
    with tempfile.TemporaryDirectory() as directory:
        output = Path(directory) / ("pattern." + suffix.lower())
        writer(pattern, str(output))
        return output.read_bytes()


@pytest.mark.parametrize("writer,suffix", [(pyembroidery.write_dst, "DST"), (pyembroidery.write_pes, "PES")])
def test_analyzes_embroidery_file(writer, suffix):
    payload = stitch_file(writer, suffix)
    response = client.post("/api/v1/preview/analizar", headers=HEADERS,
                           files={"archivo": ("sample." + suffix.lower(), payload, "application/octet-stream")})
    assert response.status_code == 200
    data = response.json()
    assert data["puntadas"] > 0
    assert data["anchoMm"] > 0 and data["altoMm"] > 0
    assert data["cantidadColores"] >= 1
    for key in ("previewPngBase64", "miniaturaPngBase64"):
        image = base64.b64decode(data[key])
        assert image.startswith(b"\x89PNG\r\n\x1a\n")


def test_rejects_missing_and_invalid_internal_token():
    payload = stitch_file(pyembroidery.write_dst, "DST")
    files = {"archivo": ("sample.dst", payload, "application/octet-stream")}
    assert client.post("/api/v1/preview/analizar", files=files).status_code == 401
    assert client.post("/api/v1/preview/analizar", headers={"X-Internal-Token": "wrong"}, files=files).status_code == 401


def test_rejects_bad_extension_and_corrupt_content():
    assert client.post("/api/v1/preview/analizar", headers=HEADERS,
                       files={"archivo": ("sample.txt", b"bad", "text/plain")}).status_code == 415
    response = client.post("/api/v1/preview/analizar", headers=HEADERS,
                           files={"archivo": ("sample.dst", b"broken", "application/octet-stream")})
    assert response.status_code == 422

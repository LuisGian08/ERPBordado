FROM python:3.12-slim AS builder
WORKDIR /build
COPY requirements.txt .
RUN pip install --no-cache-dir --prefix=/install -r requirements.txt

FROM python:3.12-slim
ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1 PORT_PREVIEW=8101
RUN useradd --system --uid 10001 --create-home preview
WORKDIR /app
COPY --from=builder /install /usr/local
COPY app ./app
USER preview
EXPOSE 8101
HEALTHCHECK --interval=15s --timeout=3s --retries=5 CMD python -c "import os,urllib.request; urllib.request.urlopen('http://127.0.0.1:'+os.getenv('PORT_PREVIEW','8101')+'/health', timeout=2)"
CMD ["sh", "-c", "uvicorn app.main:app --host 0.0.0.0 --port ${PORT_PREVIEW:-8101}"]

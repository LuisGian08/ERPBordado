package com.embroiderydash.disenos.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ArchivoTemporalServiceTest {
    @TempDir Path storage;

    @Test
    void enviaArchivoDstComoMultipartAlPreviewSimuladoYEscribeArchivoTemporal() throws Exception {
        byte[] dst = new byte[20_789];
        for (int i = 0; i < dst.length; i++) dst[i] = (byte) (i % 127);
        var png = Base64.getEncoder().encodeToString(new byte[]{(byte) 137, 80, 78, 71});
        var body = "{\"puntadas\":6689,\"anchoMm\":54.0,\"altoMm\":76.5,\"cantidadColores\":5,\"previewPngBase64\":\"" + png + "\",\"miniaturaPngBase64\":\"" + png + "\"}";
        HttpServer preview = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        preview.createContext("/api/v1/preview/analizar", exchange -> {
            byte[] multipart = exchange.getRequestBody().readAllBytes();
            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            assertNotNull(contentType);
            assertTrue(contentType.startsWith("multipart/form-data; boundary="));
            assertTrue(exchange.getRequestHeaders().getFirst("X-Internal-Token").equals("test-token"));
            assertTrue(new String(multipart, StandardCharsets.ISO_8859_1).contains("filename=\"upload.dst\""));
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            byte[] response = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        preview.start();
        try {
            var service = new ArchivoTemporalService(storage.toString(), "http://127.0.0.1:" + preview.getAddress().getPort(), "test-token", new ObjectMapper(), mock(JdbcTemplate.class), 25);
            var result = service.analizar("sample.dst", dst);
            assertEquals(6689, result.puntadas());
            assertEquals(54.0, result.anchoMm().doubleValue());
            assertEquals(76.5, result.altoMm().doubleValue());
            assertEquals(5, result.cantidadColores());
            try (var files = Files.list(storage.resolve("uploads-tmp"))) {
                assertEquals(2, files.count(), "Debe persistirse el archivo y sus metadatos para confirmar el diseño");
            }
        } finally {
            preview.stop(0);
        }
    }
}

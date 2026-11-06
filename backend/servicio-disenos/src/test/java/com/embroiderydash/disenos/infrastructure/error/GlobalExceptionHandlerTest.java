package com.embroiderydash.disenos.infrastructure.error;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void multipartMalformadoSeDevuelveComo400ConPath() {
        MockHttpServletRequest request = request();
        var response = handler.handleMultipart(new MultipartException("malformed"), request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("MULTIPART_INVALIDO", response.getBody().getCode());
        assertEquals("/api/v1/disenos/archivos/analizar", response.getBody().getPath());
    }

    @Test
    void excesoDeTamanoSeDevuelveComo413() {
        var response = handler.handleUploadTooLarge(new MaxUploadSizeExceededException(1024L), request());
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertEquals("ARCHIVO_DEMASIADO_GRANDE", response.getBody().getCode());
    }

    @Test
    void conflictoDeIntegridadSeDevuelveComo409() {
        var response = handler.handleIntegrity(new DataIntegrityViolationException("duplicate"), request());
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONFLICTO_DE_DATOS", response.getBody().getCode());
    }

    @Test
    void parteMultipartAusenteSeDevuelveComo400() {
        var response = handler.handleMissingPart(new MissingServletRequestPartException("archivo"), request());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("PARTE_MULTIPART_REQUERIDA", response.getBody().getCode());
    }

    @Test
    void tipoDeContenidoIncompatibleSeDevuelveComo415() {
        var exception = new HttpMediaTypeNotSupportedException(MediaType.APPLICATION_JSON, java.util.List.of(MediaType.MULTIPART_FORM_DATA));
        var response = handler.handleUnsupportedMedia(exception, request());
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertEquals("TIPO_CONTENIDO_NO_SOPORTADO", response.getBody().getCode());
    }

    @Test
    void cuerpoJsonInvalidoSeDevuelveComo400() {
        var response = handler.handleUnreadableBody(new HttpMessageNotReadableException("bad json"), request());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("CUERPO_INVALIDO", response.getBody().getCode());
    }

    private MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/disenos/archivos/analizar");
        return request;
    }
}

package com.embroiderydash.trabajos.infrastructure.error;

import com.embroiderydash.trabajos.domain.exception.EstadoTrabajoInvalidoException;
import com.embroiderydash.trabajos.domain.exception.ClienteNotFoundException;
import com.embroiderydash.trabajos.domain.exception.TrabajoDuplicadoException;
import com.embroiderydash.trabajos.domain.exception.TrabajoNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClienteNotFound(ClienteNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                "CLIENTE_NO_ENCONTRADO", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TrabajoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTrabajoNotFound(TrabajoNotFoundException ex, HttpServletRequest request) {
        log.warn("Orden de trabajo no encontrada: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "TRABAJO_NO_ENCONTRADO",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EstadoTrabajoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleEstadoInvalido(EstadoTrabajoInvalidoException ex, HttpServletRequest request) {
        log.warn("Transición de estado inválida: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                "TRANSICION_ESTADO_INVALIDA",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(TrabajoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleTrabajoDuplicado(TrabajoDuplicadoException ex, HttpServletRequest request) {
        log.warn("Código de orden de trabajo duplicado: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "CODIGO_TRABAJO_DUPLICADO",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "ERROR_VALIDACION_CAMPOS",
                "Uno o más campos enviados en la orden de trabajo son inválidos",
                request.getRequestURI()
        );
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acceso denegado en {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "ACCESO_DENEGADO",
                "No cuenta con los permisos necesarios para realizar esta operación",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en servicio de trabajos en {}: ", request.getRequestURI(), ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "ERROR_INTERNO_SERVIDOR",
                "Ha ocurrido un error inesperado al procesar la solicitud",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

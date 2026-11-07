package com.embroiderydash.inventario.infrastructure.error;

import com.embroiderydash.inventario.domain.exception.HiloDuplicadoException;
import com.embroiderydash.inventario.domain.exception.HiloNotFoundException;
import com.embroiderydash.inventario.domain.exception.StockInsuficienteException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleStatus(ResponseStatusException ex, HttpServletRequest request) {
        int status=ex.getStatusCode().value();
        ErrorResponse error=new ErrorResponse(status,ex.getStatusCode().toString(),"SOLICITUD_INVALIDA",ex.getReason()==null?"Solicitud inválida":ex.getReason(),request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadInput(IllegalArgumentException ex,HttpServletRequest request) {
        ErrorResponse error=new ErrorResponse(400,"Bad Request","IMAGEN_INVALIDA",ex.getMessage(),request.getRequestURI());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HiloNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHiloNotFound(HiloNotFoundException ex, HttpServletRequest request) {
        log.warn("Hilo no encontrado: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "HILO_NO_ENCONTRADO",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(StockInsuficienteException ex, HttpServletRequest request) {
        log.warn("Operación denegada por stock insuficiente: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                "STOCK_INSUFICIENTE",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(HiloDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleHiloDuplicado(HiloDuplicadoException ex, HttpServletRequest request) {
        log.warn("Violación de unicidad de hilo: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "HILO_DUPLICADO",
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
                "Uno o más campos enviados en la solicitud son inválidos",
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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                "METODO_NO_PERMITIDO", "El mÃ©todo HTTP no estÃ¡ habilitado para esta ruta", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                "RUTA_NO_ENCONTRADA", "La ruta solicitada no existe en este servicio", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en servicio de inventario en {}: ", request.getRequestURI(), ex);
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

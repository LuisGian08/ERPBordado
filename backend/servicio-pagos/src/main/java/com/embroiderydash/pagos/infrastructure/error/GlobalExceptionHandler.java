package com.embroiderydash.pagos.infrastructure.error;

import com.embroiderydash.pagos.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log=LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private ResponseEntity<ErrorResponse> error(HttpStatus status,String code,String message,HttpServletRequest req){
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(),status.getReasonPhrase(),code,message,req.getRequestURI()));
    }
    @ExceptionHandler(PagoExcedeSaldoException.class) public ResponseEntity<ErrorResponse> saldo(PagoExcedeSaldoException ex,HttpServletRequest req){return error(HttpStatus.CONFLICT,"PAGO_EXCEDE_SALDO",ex.getMessage(),req);}
    @ExceptionHandler(PagoNotFoundException.class) public ResponseEntity<ErrorResponse> pago(PagoNotFoundException ex,HttpServletRequest req){return error(HttpStatus.NOT_FOUND,"PAGO_NO_ENCONTRADO",ex.getMessage(),req);}
    @ExceptionHandler(TrabajoNoDisponibleException.class) public ResponseEntity<ErrorResponse> trabajo(TrabajoNoDisponibleException ex,HttpServletRequest req){return error(HttpStatus.UNPROCESSABLE_ENTITY,"TRABAJO_NO_DISPONIBLE",ex.getMessage(),req);}
    @ExceptionHandler(IdempotencyConflictException.class) public ResponseEntity<ErrorResponse> idempotencia(IdempotencyConflictException ex,HttpServletRequest req){return error(HttpStatus.CONFLICT,"IDEMPOTENCY_CONFLICT",ex.getMessage(),req);}
    @ExceptionHandler(TrabajosServiceUnavailableException.class) public ResponseEntity<ErrorResponse> trabajos(TrabajosServiceUnavailableException ex,HttpServletRequest req){return error(HttpStatus.SERVICE_UNAVAILABLE,"SERVICIO_TRABAJOS_NO_DISPONIBLE",ex.getMessage(),req);}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex,HttpServletRequest req){
        ErrorResponse body=new ErrorResponse(400,"Bad Request","ERROR_VALIDACION_CAMPOS","Uno o más campos enviados en el pago son inválidos",req.getRequestURI());
        for(FieldError field:ex.getBindingResult().getFieldErrors())body.addValidationError(field.getField(),field.getDefaultMessage());
        return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler(MissingRequestHeaderException.class) public ResponseEntity<ErrorResponse> cabecera(MissingRequestHeaderException ex,HttpServletRequest req){return error(HttpStatus.BAD_REQUEST,"CABECERA_REQUERIDA","Falta la cabecera requerida: "+ex.getHeaderName(),req);}
    @ExceptionHandler(HttpMessageNotReadableException.class) public ResponseEntity<ErrorResponse> bodyInvalido(HttpMessageNotReadableException ex,HttpServletRequest req){return error(HttpStatus.BAD_REQUEST,"SOLICITUD_INVALIDA","El cuerpo de la solicitud tiene un formato o valor no válido",req);}
    @ExceptionHandler(ResponseStatusException.class) public ResponseEntity<ErrorResponse> status(ResponseStatusException ex,HttpServletRequest req){
        HttpStatus status=HttpStatus.valueOf(ex.getStatusCode().value());String reason=ex.getReason()==null?status.getReasonPhrase():ex.getReason();
        String code=reason.startsWith("DISENO_SIN_PRECIO")?"DISENO_SIN_PRECIO":status.is4xxClientError()?"SOLICITUD_INVALIDA":"ERROR_INTERNO_SERVIDOR";
        return error(status,code,reason,req);
    }
    @ExceptionHandler(AccessDeniedException.class) public ResponseEntity<ErrorResponse> denied(AccessDeniedException ex,HttpServletRequest req){return error(HttpStatus.FORBIDDEN,"ACCESO_DENEGADO","No cuenta con los permisos necesarios para realizar esta operación",req);}
    @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex,HttpServletRequest req){
        log.warn("Conflicto de integridad en pagos: {}",ex.getMessage());return error(HttpStatus.CONFLICT,"CONFLICTO_INTEGRIDAD","La operación entra en conflicto con un registro existente",req);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) public ResponseEntity<ErrorResponse> methodNotAllowed(HttpRequestMethodNotSupportedException ex,HttpServletRequest req){return error(HttpStatus.METHOD_NOT_ALLOWED,"METODO_NO_PERMITIDO","El mÃ©todo HTTP no estÃ¡ habilitado para esta ruta",req);}
    @ExceptionHandler(Exception.class) public ResponseEntity<ErrorResponse> general(Exception ex,HttpServletRequest req){
        log.error("Error inesperado en servicio de pagos en {}",req.getRequestURI(),ex);return error(HttpStatus.INTERNAL_SERVER_ERROR,"ERROR_INTERNO_SERVIDOR","Ha ocurrido un error inesperado al procesar la solicitud",req);
    }
}

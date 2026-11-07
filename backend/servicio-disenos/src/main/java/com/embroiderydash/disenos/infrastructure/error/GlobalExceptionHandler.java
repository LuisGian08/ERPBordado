package com.embroiderydash.disenos.infrastructure.error;

import com.embroiderydash.disenos.domain.exception.DisenoDuplicadoException;
import com.embroiderydash.disenos.domain.exception.DisenoNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleStatus(ResponseStatusException ex,HttpServletRequest request) {
        int status=ex.getStatusCode().value();
        String code=status==413?"ARCHIVO_DEMASIADO_GRANDE":status==415?"FORMATO_NO_SOPORTADO":status==422?"ARCHIVO_INVALIDO":status==503?"SERVICIO_PREVIEW_NO_DISPONIBLE":status==409?"CONFLICTO_DE_DATOS":status==404?"RECURSO_NO_ENCONTRADO":"SOLICITUD_INVALIDA";
        return ResponseEntity.status(status).body(new ErrorResponse(status,ex.getStatusCode().toString(),code,ex.getReason()==null?"Solicitud invÃ¡lida":ex.getReason(),request.getRequestURI()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleUploadTooLarge(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return clientError(HttpStatus.PAYLOAD_TOO_LARGE, "ARCHIVO_DEMASIADO_GRANDE", "El archivo supera el tamaÃ±o mÃ¡ximo permitido", request);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPart(MissingServletRequestPartException ex, HttpServletRequest request) {
        return clientError(HttpStatus.BAD_REQUEST, "PARTE_MULTIPART_REQUERIDA", "Falta el archivo requerido en la solicitud", request);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipart(MultipartException ex, HttpServletRequest request) {
        return clientError(HttpStatus.BAD_REQUEST, "MULTIPART_INVALIDO", "No se pudo leer el formulario de carga; verifica el archivo y el formato multipart", request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMedia(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return clientError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "TIPO_CONTENIDO_NO_SOPORTADO", "El tipo de contenido no estÃ¡ soportado; envÃ­a el archivo como multipart/form-data", request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return clientError(HttpStatus.BAD_REQUEST, "CUERPO_INVALIDO", "El cuerpo de la solicitud estÃ¡ vacÃ­o o tiene un formato invÃ¡lido", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Conflicto de integridad en {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return clientError(HttpStatus.CONFLICT, "CONFLICTO_DE_DATOS", "La operaciÃ³n entra en conflicto con datos existentes o relacionados", request);
    }

    private ResponseEntity<ErrorResponse> clientError(HttpStatus status, String code, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), code, message, request.getRequestURI()));
    }

    @ExceptionHandler(DisenoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDisenoNotFound(DisenoNotFoundException ex, HttpServletRequest request) {
        log.warn("DiseÃ±o no encontrado: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "DISENO_NO_ENCONTRADO",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DisenoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDisenoDuplicado(DisenoDuplicadoException ex, HttpServletRequest request) {
        log.warn("ViolaciÃ³n de regla de unicidad de diseÃ±o: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "DISENO_DUPLICADO",
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
                "Uno o mÃ¡s campos enviados no cumplen con las reglas de validaciÃ³n",
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
                "No cuenta con los permisos necesarios para realizar esta operaciÃ³n",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado procesando solicitud en {}: ", request.getRequestURI(), ex);
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

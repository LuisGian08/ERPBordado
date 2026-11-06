package com.embroiderydash.trabajos.infrastructure.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private List<ValidationErrorDetail> validationErrors;

    public ErrorResponse() {
        this.timestamp = OffsetDateTime.now();
    }

    public ErrorResponse(int status, String error, String code, String message, String path) {
        this.timestamp = OffsetDateTime.now();
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public void addValidationError(String field, String message) {
        if (this.validationErrors == null) {
            this.validationErrors = new ArrayList<>();
        }
        this.validationErrors.add(new ValidationErrorDetail(field, message));
    }

    public static class ValidationErrorDetail {
        private String field;
        private String message;

        public ValidationErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ValidationErrorDetail> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationErrorDetail> validationErrors) {
        this.validationErrors = validationErrors;
    }
}

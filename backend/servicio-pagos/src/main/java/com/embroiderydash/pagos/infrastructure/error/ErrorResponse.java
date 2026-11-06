package com.embroiderydash.pagos.infrastructure.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private OffsetDateTime timestamp=OffsetDateTime.now(); private int status; private String error; private String code; private String message; private String path;
    private List<ValidationErrorDetail> validationErrors;
    public ErrorResponse() {}
    public ErrorResponse(int status,String error,String code,String message,String path){this.status=status;this.error=error;this.code=code;this.message=message;this.path=path;}
    public void addValidationError(String field,String message){if(validationErrors==null)validationErrors=new ArrayList<>();validationErrors.add(new ValidationErrorDetail(field,message));}
    public OffsetDateTime getTimestamp(){return timestamp;} public int getStatus(){return status;} public void setStatus(int v){status=v;}
    public String getError(){return error;} public void setError(String v){error=v;} public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getMessage(){return message;} public void setMessage(String v){message=v;} public String getPath(){return path;} public void setPath(String v){path=v;}
    public List<ValidationErrorDetail> getValidationErrors(){return validationErrors;} public void setValidationErrors(List<ValidationErrorDetail> v){validationErrors=v;}
    public static class ValidationErrorDetail {
        private String field; private String message;
        public ValidationErrorDetail(String field,String message){this.field=field;this.message=message;}
        public String getField(){return field;} public String getMessage(){return message;}
    }
}

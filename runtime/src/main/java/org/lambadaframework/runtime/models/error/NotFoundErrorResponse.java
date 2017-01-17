package org.lambadaframework.runtime.models.error;



public class NotFoundErrorResponse extends ErrorResponse {

    protected String errorMessage;

    public NotFoundErrorResponse() {
        this.entity = "Page not found";
        this.code = 404;
    }

    public NotFoundErrorResponse(String errorMessage) {
        this();
        this.entity = errorMessage;
    }
}

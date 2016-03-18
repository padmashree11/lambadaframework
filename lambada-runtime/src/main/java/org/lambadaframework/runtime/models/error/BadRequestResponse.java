package org.lambadaframework.runtime.models.error;


public class BadRequestResponse extends ErrorResponse {

    protected String errorMessage;

    public BadRequestResponse() {
        this.errorMessage = "Bad request";
        this.code = 400;
    }

    public BadRequestResponse(String errorMessage) {
        this();
        this.errorMessage = errorMessage;
    }
}

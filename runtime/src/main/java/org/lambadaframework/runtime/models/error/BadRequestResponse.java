package org.lambadaframework.runtime.models.error;


public class BadRequestResponse extends ErrorResponse {


    public BadRequestResponse() {
        this.entity = "Bad request";
        this.code = 400;
    }

    public BadRequestResponse(String errorMessage) {
        this();
        this.entity = errorMessage;
    }
}

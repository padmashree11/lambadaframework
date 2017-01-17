package org.lambadaframework.runtime.models.error;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.lambadaframework.runtime.models.Response;
import org.lambadaframework.runtime.models.ResponseProxy;

public class ErrorResponse extends ResponseProxy {

    protected String errorMessage;

    public ErrorResponse() {
        this.entity = "Internal Server Error";
        this.code = 500;
    }

    public ErrorResponse(String errorMessage) {
        this();
        this.entity = errorMessage;
    }
}

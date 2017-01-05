package org.lambadaframework.runtime;

import org.lambadaframework.runtime.models.Request;
import org.lambadaframework.runtime.models.RequestInterface;

import javax.ws.rs.core.MediaType;
import java.util.Map;

public class RequestProxy implements RequestInterface {

    @Override
    public Request.RequestMethod getMethod() {
        return null;
    }

    @Override
    public String getPathTemplate() {
        return null;
    }

    @Override
    public String getPackage() {
        return null;
    }

    @Override
    public MediaType getConsumedMediaType() {
        return null;
    }

    @Override
    public MediaType getProducedMediaType() {
        return null;
    }

    @Override
    public Map<String, String> getPathParameters() {
        return null;
    }

    @Override
    public Map<String, String> getQueryParams() {
        return null;
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        return null;
    }

    @Override
    public String getRequestBody() {
        return null;
    }
}

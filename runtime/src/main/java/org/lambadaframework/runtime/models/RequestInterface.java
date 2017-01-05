package org.lambadaframework.runtime.models;

import javax.ws.rs.core.MediaType;
import java.util.Map;

public interface RequestInterface {

    Request.RequestMethod getMethod();

    String getPathTemplate();

    String getPackage();

    MediaType getConsumedMediaType();

    MediaType getProducedMediaType();

    Map<String, String> getPathParameters();

    Map<String, String> getQueryParams();

    Map<String, String> getRequestHeaders();

    String getRequestBody();

}

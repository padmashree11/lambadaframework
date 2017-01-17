package org.lambadaframework.runtime.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseProxy implements Serializable {

    private static final Logger logger = Logger.getLogger(ResponseProxy.class);

    /**
     * Response headers
     */
    protected Map<String, String> headers;

    /**
     * Status code
     */
    protected int code = 200;

    /**
     * Response entity
     */
    protected Object entity;

    public static void buildAndWriteFromJAXRSResponse(Object response, Writer writer) throws RuntimeException, IOException {

        ResponseProxy outputResponse = new ResponseProxy();

        if (response instanceof javax.ws.rs.core.Response) {

            javax.ws.rs.core.Response JAXResponse = ((javax.ws.rs.core.Response) response);

            int status = JAXResponse.getStatus();

            outputResponse.entity = JAXResponse.getEntity();
            outputResponse.code = status;
            outputResponse.headers = new LinkedHashMap<>();

            for (Map.Entry<String, List<Object>> entry : JAXResponse.getHeaders().entrySet()) {
                outputResponse.headers.put(entry.getKey(), (String) entry.getValue().get(0));
            }


        } else {
            outputResponse.entity = response;
        }

        outputResponse.write(writer);
    }

    @JsonProperty("body")
    public Object getEntity() {
        return entity;
    }

    @JsonProperty("headers")
    public Map<String, String> getHeaders() {
        return headers;
    }

    @JsonProperty("statusCode")
    public int getStatusCode() {
        return this.code;
    }

    private void write(Writer writer) throws IOException {
        new ObjectMapper().writeValue(writer, this);
        writer.close();
    }

}

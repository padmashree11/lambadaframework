package org.lambadaframework.runtime;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lambadaframework.jaxrs.model.ResourceMethod;
import org.lambadaframework.runtime.errorhandling.ErrorHandler;
import org.lambadaframework.runtime.models.RequestInterface;
import org.lambadaframework.runtime.models.Response;
import org.lambadaframework.runtime.router.Router;

import java.io.*;
import java.util.Map;
import java.util.Set;


public class Handler implements RequestStreamHandler {

    static final Logger logger = Logger.getLogger(Handler.class);
    JSONParser parser = new JSONParser();

    private Router router;


    public Handler setRouter(Router router) {
        this.router = router;
        return this;
    }

    public Router getRouter() {
        if (router != null) {
            return router;
        }
        return Router.getRouter();
    }

    /**
     * If request object's "method" field is null or has an invalid
     * HTTP method string it is impossible to process the request
     * thus we throw an exception and 500 HTTP error.
     *
     * @param requestObject Request object
     * @throws Exception
     */
    private void checkHttpMethod(RequestInterface requestObject)
            throws Exception {
        if (requestObject.getMethod() == null) {
            throw new Exception("Method was null");
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {

        logger.debug("Loading Java Lambda handler of ProxyWithStream");

        Object invoke;
        try {
            RequestInterface req = getParsedRequest(inputStream);

            if (req == null) {
                logger.debug("Request object is null can not proceed with request.");
            } else {
                checkHttpMethod(req);
                logger.debug("Request check is ok.");
                ResourceMethod matchedResourceMethod = getRouter().route(req);
                invoke = ResourceMethodInvoker.invoke(matchedResourceMethod, req, context);
                Response response = Response.buildFromJAXRSResponse(invoke);
                //TODO: write a response.

            }

        } catch (Exception e) {
            logger.debug("Error: " + e.getMessage());
            ErrorHandler.getErrorResponse(e);
        }

    }


    /**
     * @return a ObjectMapper configured to ignore if incoming json do have properties unknown to requestProxy.
     */
    private ObjectMapper getConfiguredMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    private RequestInterface getParsedRequest(InputStream inputStream) {
        logger.debug("Starting to parse request stream");

        try {
            JsonParser jp = new JsonFactory().createParser(inputStream);
            RequestInterface req = getConfiguredMapper().readValue(jp, RequestProxy.class);
            logger.debug("Parsed input stream to Request object");
            logger.debug("Closing parser");
            jp.close();
            return req;

        } catch (IOException e) {
            logger.debug("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.debug("Error:" + e.getMessage());
        }

        return null;
    }

}

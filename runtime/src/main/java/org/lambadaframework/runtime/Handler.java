package org.lambadaframework.runtime;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lambadaframework.runtime.models.RequestInterface;
import org.lambadaframework.runtime.router.Router;

import java.io.*;


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


//    @Override
//    public Response handleRequest(RequestInterface request, Context context) {
//        Object invoke;
//        try {
//            logger.debug("Request started with " + request + " and " + context);
//
//            checkHttpMethod(request);
//            logger.debug("Request check is ok.");
//
//            logger.debug("Matching request to a resource handler.");
//            ResourceMethod matchedResourceMethod = getRouter().route(request);
//            invoke = ResourceMethodInvoker.invoke(matchedResourceMethod, request, context);
//
//        } catch (Exception ex) {
//            return ErrorHandler.getErrorResponse(ex);
//        }
//        logger.debug("Returning result.");
//        return Response.buildFromJAXRSResponse(invoke);
//    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

//        LambdaLogger logger = context.getLogger();
        logger.debug("Loading Java Lambda handler of ProxyWithStream");


        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        String name = "World";
        String responseCode = "200";

        try {
            JSONObject event = (JSONObject) parser.parse(reader);
            if (event.get("queryStringParameters") != null) {
                JSONObject qps = (JSONObject) event.get("queryStringParameters");
                if (qps.get("name") != null) {
                    name = (String) qps.get("name");
                }
                if (qps.get("httpStatus") != null) {
                    responseCode = qps.get("httpStatus)").toString();
                }
            }


            JSONObject responseBody = new JSONObject();
            responseBody.put("input", event.toJSONString());
            responseBody.put("message", "APA " + name + "!");

            JSONObject headerJson = new JSONObject();
            headerJson.put("x-custom-response-header", "my custom response header value");

            responseJson.put("statusCode", responseCode);
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody.toJSONString());

        } catch (ParseException pex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", pex);
        }

//        logger.debug(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        String test = "{\"statusCode\": 200, \"headers\": { \"headerName\": \"headerValue\"}, \"body\": \"...\"}";
        logger.debug(test);
//        writer.write(responseJson.toJSONString());
        writer.write(test);
        writer.close();
    }

}

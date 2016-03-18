package org.lambadaframework.runtime;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.lambadaframework.runtime.errorhandling.ErrorHandler;
import org.lambadaframework.runtime.logging.LambdaLogger;
import org.lambadaframework.runtime.models.Request;
import org.lambadaframework.runtime.models.Response;
import org.lambadaframework.runtime.router.Router;
import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.RuntimeDelegateImpl;
import org.glassfish.jersey.server.model.*;

public class Handler
        implements RequestHandler<Request, Response> {

    static final Logger logger = LambdaLogger.getLogger(Handler.class);

    static {
        /**
         * The worst hack ever.
         *
         * We need this dynamically loaded class but maven-shade plugin does not include it
         * on minimize stage. So we try to declare it here manually although it causes an exception.
         */
        try {
            RuntimeDelegateImpl.getInstance();
        } catch (Exception e) {
            /**
             * DO NOTHING
             */
        }

    }

    /**
     * If request object's "method" field is null or has an invalid
     * HTTP method string it is impossible to process the request
     * thus we throw an exception and 500 HTTP error.
     *
     * @param requestObject Request object
     * @throws Exception
     */
    private void checkHttpMethod(Request requestObject)
            throws Exception {
        if (requestObject.getMethod() == null) {
            throw new Exception("Method was null");
        }
    }


    @Override
    public Response handleRequest(Request request, Context context) {

        try {
            logger.debug("Request started with " + request + " and " + context);

            checkHttpMethod(request);
            logger.debug("Request check is ok.");

            logger.debug("Matching request to a resource handler.");
            ResourceMethod matchedResourceMethod = Router.getRouter().route(request);

            logger.debug("Returning result.");
            return Response.buildFromJAXRSResponse(ResourceMethodInvoker.invoke(matchedResourceMethod, request, context));
        } catch (Exception ex) {
            return ErrorHandler.getErrorResponse(ex);
        }
    }

    /**
     * This method is only used by local lambda runner.
     *
     * @return String
     */
    @SuppressWarnings("unused")
    public String getExampleEvent() {
        String s = "{\n" +
                "\t\"package\": \"com.cagataygurturk.testlambdahandlers\",\n" +
                "\t\"pathTemplate\": \"/resource1/{id}/users\",\n" +
                "\t\"method\": \"GET\",\n" +
                "\t\"requestBody\": {\n" +
                "\t\t\"test\": \"test2\",\n" +
                "\t\t\"test2\": {\n" +
                "\t\t\t\"test3\": \"test4\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"path\": {\n" +
                "\t\t\"id\": \"123\"\n" +
                "\t},\n" +
                "\t\"querystring\": {\n" +
                "\t\t\"query1\": \"test1\",\n" +
                "\t\t\"query2\": \"test2\"\n" +
                "\t},\n" +
                "\t\"header\": {\n" +
                "\t\t\"X-Device-Id\": \"asdadadad\"\n" +
                "\t}\n" +
                "}";

        return s;
    }
}
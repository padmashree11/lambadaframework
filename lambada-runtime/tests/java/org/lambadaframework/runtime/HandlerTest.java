package org.lambadaframework.runtime;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.lambadaframework.runtime.models.Request;
import org.lambadaframework.runtime.models.Response;
import org.lambadaframework.testlambdahandlers.TestHandler;

import static org.junit.Assert.*;

public class HandlerTest {


    private Request getRequest(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Request.class);
    }

    private Context getContext() {
        return new Context() {
            @Override
            public String getAwsRequestId() {
                return "23234234";
            }

            @Override
            public String getLogGroupName() {
                return null;
            }

            @Override
            public String getLogStreamName() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }

            @Override
            public String getFunctionVersion() {
                return null;
            }

            @Override
            public String getInvokedFunctionArn() {
                return null;
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 5000;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 128;
            }

            @Override
            public LambdaLogger getLogger() {
                return null;
            }
        };
    }


    @Test
    public void testWith200Result()
            throws Exception {

        Request exampleRequest = getRequest("{\n" +
                "  \"package\": \"org.lambadaframework.testlambdahandlers\",\n" +
                "  \"pathTemplate\": \"/{id}\",\n" +
                "  \"method\": \"GET\",\n" +
                "  \"requestBody\": {},\n" +
                "  \"path\": {\n" +
                "    \"id\": \"123\"\n" +
                "  },\n" +
                "  \"querystring\": {\n" +
                "        \"query1\": \"test3\",\n" +
                "    \"query2\": \"test\"\n" +
                "  },\n" +
                "  \"header\": {}\n" +
                "}");

        Handler handler = new Handler();
        Response response = handler.handleRequest(exampleRequest, getContext());

        assertEquals(200, response.getCode());
        assertEquals("cagatay gurturk", ((TestHandler.Entity) response.getEntity()).query1);
        assertEquals(123, ((TestHandler.Entity) response.getEntity()).id);

    }


    @Test
    public void testWith201Result()
            throws Exception {

        Request exampleRequest = getRequest("{\n" +
                "  \"package\": \"org.lambadaframework.testlambdahandlers\",\n" +
                "  \"pathTemplate\": \"/{id}\",\n" +
                "  \"method\": \"POST\",\n" +
                "  \"requestBody\": {},\n" +
                "  \"path\": {\n" +
                "    \"id\": \"123\"\n" +
                "  },\n" +
                "  \"querystring\": {\n" +
                "        \"query1\": \"test3\",\n" +
                "    \"query2\": \"test\"\n" +
                "  },\n" +
                "  \"header\": {}\n" +
                "}");

        Handler handler = new Handler();
        Response response = handler.handleRequest(exampleRequest, getContext());

        assertEquals(201, response.getCode());
        assertEquals("test3", ((TestHandler.Entity) response.getEntity()).query1);
        assertEquals(123, ((TestHandler.Entity) response.getEntity()).id);
        assertEquals("http://www.google.com", response.getHeaders().get("Location"));

    }



}
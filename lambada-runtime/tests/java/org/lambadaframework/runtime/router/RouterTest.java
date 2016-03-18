package org.lambadaframework.runtime.router;


import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.junit.Test;
import org.lambadaframework.runtime.models.Request;

import java.util.List;

import static org.junit.Assert.*;


public class RouterTest {

    @Test
    public void testGetJAXRSResourcesFromPackage() throws Exception {


        List<Resource> resourceList = Router.getRouter().getJAXRSResourcesFromPackage("org.lambadaframework.testlambdahandlers");
        assertTrue(resourceList.size() > 0);
    }

    @Test
    public void getRouter() throws Exception {


        Request request = new Request();
        request
                .setMethod(Request.RequestMethod.GET)
                .setPackage("org.lambadaframework")
                .setPathtemplate("/{id}");

        ResourceMethod routedResource = Router.getRouter().route(request);

        assertNotNull(routedResource);
    }
}
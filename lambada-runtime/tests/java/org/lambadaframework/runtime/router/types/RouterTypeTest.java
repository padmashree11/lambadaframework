package org.lambadaframework.runtime.router.types;

import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.junit.Test;
import org.lambadaframework.runtime.models.Request;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.*;


public class RouterTypeTest {


    private Request getRequest() {
        Request request = new Request();
        request.setPackage("com.cagataygurturk.testhandlers");
        request.setMethod(Request.RequestMethod.GET);
        request.setConsumes(MediaType.APPLICATION_JSON);
        request.setProduces(MediaType.APPLICATION_JSON);
        request.setPathtemplate("helloworld/{id}");
        return request;
    }

    private ResourceMethod getResourceMethod() {

        Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path("helloworld/{id}");
        ResourceMethod resourceMethod = resourceBuilder
                .addMethod("GET")
                .consumes(MediaType.APPLICATION_JSON_TYPE)
                .produces(MediaType.APPLICATION_JSON_TYPE)
                .handledBy(new Inflector<ContainerRequestContext, Object>() {
                    @Override
                    public Object apply(ContainerRequestContext containerRequestContext) {
                        return "HELLO";
                    }
                })
                .build();

        Resource resource = resourceBuilder
                .build();

        return resource.getResourceMethods().get(0);
    }

    @Test
    public void consumedTypesTest() throws Exception {
        RouterType routerType = new ConsumedTypes();
        assertTrue(routerType.isMatching(getRequest(), getResourceMethod()));
    }

    @Test
    public void producedTypesTest() throws Exception {
        RouterType routerType = new ProducedTypes();
        assertTrue(routerType.isMatching(getRequest(), getResourceMethod()));
    }

    @Test
    public void methodTest() throws Exception {
        RouterType routerType = new Method();
        assertTrue(routerType.isMatching(getRequest(), getResourceMethod()));
    }

    @Test
    public void pathTest() throws Exception {
        RouterType routerType = new Path();
        assertTrue(routerType.isMatching(getRequest(), getResourceMethod()));
    }
}
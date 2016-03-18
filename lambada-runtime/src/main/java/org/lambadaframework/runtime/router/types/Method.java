package org.lambadaframework.runtime.router.types;


import org.glassfish.jersey.server.model.ResourceMethod;
import org.lambadaframework.runtime.models.Request;

public class Method implements RouterType {

    @Override
    public boolean isMatching(Request request, ResourceMethod resourceMethod) {
        return resourceMethod.getHttpMethod().equals(request.getMethod().name());
    }
}

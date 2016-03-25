package org.lambadaframework.runtime.router.types;


import org.glassfish.jersey.uri.UriTemplate;
import org.lambadaframework.jaxrs.model.Resource;
import org.lambadaframework.jaxrs.model.ResourceMethod;
import org.lambadaframework.runtime.models.Request;

import java.util.HashMap;

public class Path implements RouterType {

    @Override
    public boolean isMatching(Request request, ResourceMethod resourceMethod) {
        Resource resource = resourceMethod.getParent();
        String path = "";
        do {
            path = resource.getPath() + path;
            resource = resource.getParent();
        } while (resource != null);


        return new UriTemplate(path).match(request.getPathTemplate(), new HashMap<>());
    }
}

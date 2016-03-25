package org.lambadaframework.runtime.router.types;


import org.lambadaframework.jaxrs.model.ResourceMethod;
import org.lambadaframework.runtime.models.Request;


public class ConsumedTypes implements RouterType {
    @Override
    public boolean isMatching(Request request, ResourceMethod resourceMethod) {
        return resourceMethod.getConsumedTypes().contains(request.getConsumedMediaType());
    }
}

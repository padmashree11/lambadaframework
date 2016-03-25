package org.lambadaframework.jaxrs.model;


import org.glassfish.jersey.server.model.Invocable;

import javax.ws.rs.core.MediaType;
import java.util.List;

public final class ResourceMethod {


    private final org.glassfish.jersey.server.model.ResourceMethod proxied;

    private Resource parent;

    public ResourceMethod(org.glassfish.jersey.server.model.ResourceMethod proxied) {
        this.proxied = proxied;
    }

    public List<MediaType> getProducedTypes() {
        return proxied.getProducedTypes();
    }

    public List<MediaType> getConsumedTypes() {
        return proxied.getProducedTypes();
    }

    public String getHttpMethod() {
        return proxied.getHttpMethod();
    }

    public Resource getParent() {
        if (proxied.getParent() == null) {
            return null;
        }
        return parent = new Resource(proxied.getParent());
    }

    public Invocable getInvocable() {
        return proxied.getInvocable();
    }


}

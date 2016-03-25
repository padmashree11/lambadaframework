package org.lambadaframework.jaxrs.model;


import java.util.LinkedList;
import java.util.List;

/**
 * This class proxies org.glassfish.jersey.server.model.ResourceMethod
 * and extends its functionality for custom Lambada annotations.
 */
public final class Resource {

    List<Resource> childResources;
    List<ResourceMethod> resourceMethods;

    private final org.glassfish.jersey.server.model.Resource proxied;

    private Resource parent;

    public Resource(org.glassfish.jersey.server.model.Resource proxied) {
        this.proxied = proxied;
        this.setChildResources();
        this.setResourceMethods();
    }

    private Resource setChildResources() {
        childResources = new LinkedList<>();
        proxied.getChildResources().forEach(childResource -> {
            childResources.add(new Resource(childResource));
        });
        return this;
    }

    private Resource setResourceMethods() {
        resourceMethods = new LinkedList<>();
        proxied.getResourceMethods().forEach(resourceMethod -> {
            resourceMethods.add(new ResourceMethod(resourceMethod));
        });
        return this;
    }

    public List<Resource> getChildResources() {
        return childResources;
    }


    public List<ResourceMethod> getResourceMethods() {
        return resourceMethods;
    }


    public String getPath() {
        return proxied.getPath();
    }

    public Resource getParent() {
        if (proxied.getParent() == null) {
            return null;
        }
        return parent = new Resource(proxied.getParent());
    }

}

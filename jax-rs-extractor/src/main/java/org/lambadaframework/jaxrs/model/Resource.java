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

    private static final String SLASH_CHARACTER = "/";

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
        StringBuilder stringBuilder = new StringBuilder();
        org.glassfish.jersey.server.model.Resource parentResource = proxied.getParent();
        stringBuilder.append(proxied.getPath());
        while (parentResource != null) {
            stringBuilder.insert(0, parentResource.getPath());
            parentResource = parentResource.getParent();
        }

        String normalized = stringBuilder.toString().replace(SLASH_CHARACTER + SLASH_CHARACTER, SLASH_CHARACTER);

        if (normalized.endsWith(SLASH_CHARACTER) && !normalized.equals(SLASH_CHARACTER)) {
            return normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    public Resource getParent() {

        if (proxied.getParent() == null) {
            return null;
        }

        if (parent != null) {
            return parent;
        }

        return parent = new Resource(proxied.getParent());
    }

}

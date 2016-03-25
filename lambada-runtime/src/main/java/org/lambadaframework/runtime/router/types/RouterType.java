package org.lambadaframework.runtime.router.types;


import org.lambadaframework.jaxrs.model.ResourceMethod;
import org.lambadaframework.runtime.models.Request;

public interface RouterType {

    boolean isMatching(Request request, ResourceMethod resourceMethod);
}

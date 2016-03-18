package org.lambadaframework.runtime.router;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.lambadaframework.jaxrs.JAXRSParser;
import org.lambadaframework.runtime.logging.LambdaLogger;
import org.lambadaframework.runtime.models.Request;
import org.lambadaframework.runtime.router.types.*;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Router class decides which method to call
 * using values in Request object
 */
public final class Router {

    static final Logger logger = LambdaLogger.getLogger(Router.class);

    private static Router singletonInstance = null;

    private Map<String, List<Resource>> resourceMap = new ConcurrentHashMap<>();

    private Map<String, ResourceMethod> routingCache = new ConcurrentHashMap<>();


    private List<RouterType> routerTypes = new LinkedList<>();


    /**
     * Gets the singleton instance
     *
     * @return Router
     */
    public static Router getRouter() {

        if (singletonInstance != null) {
            logger.debug("Singleton router is being returned.");
            return singletonInstance;
        }

        return singletonInstance = new Router();
    }

    private Router() {
        logger.debug("Router is being initialized.");
        addRouterTypes();
    }

    private void addRouterTypes() {
        logger.debug("Adding router types.");
        routerTypes.add(new Path());
        routerTypes.add(new Method());
        //routerTypes.add(new ConsumedTypes());
        //routerTypes.add(new ProducedTypes());
        logger.debug("Router types are added.");

    }


    /**
     * Scans package for Resources
     *
     * @param packageName Package name to scan
     * @return List<Resource>
     */
    protected List<Resource> getJAXRSResourcesFromPackage(String packageName) {

        logger.debug("Package is being scanned: " + packageName);

        if (null != resourceMap.get(packageName)) {
            /**
             * This package is already scanned,
             * so return cached values
             */
            logger.debug("Returning cached resource map.");
            return resourceMap.get(packageName);
        }

        logger.debug("Cached resource map not found. Scanning package.");
        JAXRSParser jaxrsParser = new JAXRSParser().withPackageName(packageName, Router.class);
        List<Resource> foundResources = jaxrsParser.scan();
        resourceMap.put(packageName, foundResources);
        logger.debug(foundResources.size() + " resources found.");
        return foundResources;
    }


    private boolean isResourceMapMatches(Request request, ResourceMethod resourceMethod) {
        for (RouterType router : routerTypes) {
            if (!router.isMatching(request, resourceMethod)) {
                return false;
            }
        }
        return true;
    }


    private String calculateCacheKeyForRequest(Request request) {
        return request.getPathTemplate() + "-" +
                request.getMethod() + "-" +
                request.getConsumedMediaType() + "-" +
                request.getProducedMediaType();
    }

    public ResourceMethod route(Request request)
            throws NotFoundException {

        if (request.getPackage() == null) {
            throw new NotFoundException("Request should have package attribute");
        }

        String cacheKey = calculateCacheKeyForRequest(request);
        ResourceMethod foundMethod;

        logger.debug("Matching request with a corresponding resource method.");


        if ((foundMethod = routingCache.get(cacheKey)) != null) {
            logger.debug("Request is already cached: " + foundMethod.getClass().toString());
            return foundMethod;
        }

        List<Resource> foundResources = getJAXRSResourcesFromPackage(request.getPackage());

        for (Resource resource : foundResources) {
            for (ResourceMethod resourceMethod : resource.getResourceMethods()) {
                if (isResourceMapMatches(request, resourceMethod)) {
                    logger.debug("Match complete: " + resourceMethod.getClass().toString());
                    routingCache.put(cacheKey, resourceMethod);
                    return resourceMethod;
                }
            }
        }

        throw new NotFoundException();
    }
}

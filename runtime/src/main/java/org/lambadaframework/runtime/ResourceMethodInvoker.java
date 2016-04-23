package org.lambadaframework.runtime;


import com.amazonaws.services.lambda.runtime.Context;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.model.Invocable;
import org.lambadaframework.jaxrs.model.ResourceMethod;
import org.lambadaframework.logger.LambdaLogger;
import org.lambadaframework.runtime.models.Request;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ResourceMethodInvoker {


    static final Logger logger = LambdaLogger.getLogger(ResourceMethodInvoker.class);

    private ResourceMethodInvoker() {
    }

    private static Object toObject(String value, Class clazz) {
        if (Integer.class == clazz || Integer.TYPE == clazz) return Integer.parseInt(value);
        if (Long.class == clazz || Long.TYPE == clazz) return Long.parseLong(value);
        if (Float.class == clazz || Float.TYPE == clazz) return Float.parseFloat(value);
        if (Boolean.class == clazz || Boolean.TYPE == clazz) return Boolean.parseBoolean(value);
        if (Double.class == clazz || Double.TYPE == clazz) return Double.parseDouble(value);
        if (Byte.class == clazz || Byte.TYPE == clazz) return Byte.parseByte(value);
        if (Short.class == clazz || Short.TYPE == clazz) return Short.parseShort(value);
        return value;
    }

    public static Object invoke(ResourceMethod resourceMethod,
                                Request request,
                                Context lambdaContext)
            throws
            InvocationTargetException,
            IllegalAccessException,
            InstantiationException {

        logger.debug("Request object is: " + request.toString());


        Invocable invocable = resourceMethod.getInvocable();

        Method method = invocable.getHandlingMethod();
        Class clazz = invocable.getHandler().getHandlerClass();

        Object instance = clazz.newInstance();

        List<Object> varargs = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {

            Class<?> parameterClass = parameter.getType();

            /**
             * Path parameter
             */
            if (parameter.isAnnotationPresent(PathParam.class)) {
                PathParam annotation = parameter.getAnnotation(PathParam.class);
                varargs.add(toObject(
                        (String) request.getPathParameters().get(annotation.value()), parameterClass
                        )
                );

            }


            /**
             * Query parameter
             */
            if (parameter.isAnnotationPresent(QueryParam.class)) {
                QueryParam annotation = parameter.getAnnotation(QueryParam.class);
                varargs.add(toObject(
                        (String) request.getQueryParams().get(annotation.value()), parameterClass
                        )
                );
            }

            /**
             * Query parameter
             */
            if (parameter.isAnnotationPresent(HeaderParam.class)) {
                HeaderParam annotation = parameter.getAnnotation(HeaderParam.class);
                varargs.add(toObject(
                        (String) request.getRequestHeaders().get(annotation.value()), parameterClass
                        )
                );
            }


            /**
             * Lambda Context can be automatically injected
             */
            if (parameter.getType() == Context.class) {
                varargs.add(lambdaContext);
            }
        }

        return method.invoke(instance, varargs.toArray());
    }
}

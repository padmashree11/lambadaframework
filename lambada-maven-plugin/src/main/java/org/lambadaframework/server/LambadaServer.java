package org.lambadaframework.server;

import com.sun.research.ws.wadl.HTTPMethods;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.lambadaframework.AbstractMojoPlugin;
import org.lambadaframework.deployer.Deployment;
import org.lambadaframework.aws.ApiGateway;
import org.lambadaframework.jaxrs.JAXRSParser;
import org.lambadaframework.jaxrs.model.Resource;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

import static spark.Spark.*;

@Mojo(name = "serve", requiresDirectInvocation = true,
        requiresProject = true,
        defaultPhase = LifecyclePhase.INSTALL,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
@Execute(phase = LifecyclePhase.INSTALL)
public class LambadaServer extends AbstractMojoPlugin {

    @Parameter(property = "local.port", defaultValue = "8080")
    Integer serverPort;

    @Override
    public void execute() throws MojoExecutionException {
        try {


            printLogo();
            getLog().info(LOG_SEPERATOR);
            getLog().info("Starting web server at port " + serverPort);

            port(serverPort);

            threadPool(8);

            setUpServer();

            awaitInitialization();


            while (10 != System.in.read()) {
                Thread.sleep(500);
            }

            stop();
        } catch (Exception e) {
            throw new MojoExecutionException("Exception at deployment", e);
        }
    }

    private String getSparkPath(String jerseyPath) {
        return jerseyPath.replace("{", ":").replace("}", "");
    }

    private void setUpServer() {
        try {

            Deployment deployment = getDeployment();

            JAXRSParser jaxrsParser = new JAXRSParser()
                    .withJarFile(deployment.getJarFileLocationOnLocalFileSystem(), packageName);

            List<Resource> resources = jaxrsParser.scan();
            getLog().info(resources.size() + " resources has been found");


            resources.forEach(resource -> {

                final String fullPath = ApiGateway.getFullPartOfResource(resource);

                resource.getResourceMethods().forEach(resourceMethod -> {


                    getLog().info(fullPath + " is being created");

                    if (resourceMethod.getHttpMethod().equals(HTTPMethods.GET.value())) {
                        get(getSparkPath(fullPath), this::handle);
                    }

                    if (resourceMethod.getHttpMethod().equals(HTTPMethods.POST.value())) {
                        post(getSparkPath(fullPath), this::handle);
                    }

                    if (resourceMethod.getHttpMethod().equals(HTTPMethods.DELETE.value())) {
                        delete(getSparkPath(fullPath), this::handle);
                    }

                    if (resourceMethod.getHttpMethod().equals(HTTPMethods.HEAD.value())) {
                        head(getSparkPath(fullPath), this::handle);
                    }

                    if (resourceMethod.getHttpMethod().equals(HTTPMethods.PUT.value())) {
                        put(getSparkPath(fullPath), this::handle);
                    }

                });
            });


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Object handle(Request request, Response response)
            throws Exception {
        String path = request.pathInfo();
        for(Map.Entry<String, String> entry : request.params().entrySet()) {
            String paramName = entry.getKey();
            String paramValue = entry.getValue();
            path = path.replace(paramValue, paramName.replace(":", "{") + "}");
        }


        getLog().info(path);
        return "hello world";
    }

}

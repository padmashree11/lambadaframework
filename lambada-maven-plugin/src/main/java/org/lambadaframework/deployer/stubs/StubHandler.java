package org.lambadaframework.deployer.stubs;


import com.amazonaws.services.lambda.runtime.Context;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Test controller class
 * Only used for test purposes
 */
@Path("/resource1")
public class StubHandler {

    public static class NewEntityRequest {

        public long id;

        public String name;

    }

    public static class Entity {

        public long id;

        public String query1;

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getEntity(
            @PathParam("id") long id
    ) {
        Entity entity = new Entity();
        entity.id = id;
        entity.query1 = "cagatay gurturk";
        return Response
                .status(200)
                .entity(entity)
                .build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getEntity(NewEntityRequest messageRequest) {
        return Response.status(200).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/users")
    public Response getEntityUsers(
            @PathParam("id") long id,
            @QueryParam("query1") String query1,
            @HeaderParam("x-api-key") String apiKey,
            Context context
    ) {
        Entity entity = new Entity();
        entity.id = id;
        entity.query1 = query1;

        return Response
                .status(201)
                .header("Location", "http://www.google.com")
                .entity(entity)
                .build();
    }

}

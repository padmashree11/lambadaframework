package org.lambadaframework.testlambdahandlers;


import com.amazonaws.services.lambda.runtime.Context;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class TestHandler {

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
    @Path("{id}")
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
    @Path("{id}")
    public Response createEntityUsers(
            @PathParam("id") long id,
            @QueryParam("query1") String query1,
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

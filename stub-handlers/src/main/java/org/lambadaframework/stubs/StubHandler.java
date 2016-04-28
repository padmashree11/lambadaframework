package org.lambadaframework.stubs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/resource1")
public class StubHandler {

    public static class NewEntityRequest {

        public long id;

        public String name;

    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getList(@PathParam("id") long id) {
        return Response.status(200).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getEntity(@PathParam("id") long id) {
        return Response.status(200).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newEntity(NewEntityRequest requestBody) {
        return Response.status(200).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeEntity(@PathParam("id") long id) {
        return Response.status(200).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/users")
    public Response getEntityUsers(@PathParam("id") long id) {
        return Response.status(200).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/users")
    public Response removeEntityUser(@PathParam("id") long id) {
        return Response.status(200).build();
    }


}

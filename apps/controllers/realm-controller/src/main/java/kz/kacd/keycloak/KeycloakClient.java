package kz.kacd.keycloak;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/k8s-config")
public interface KeycloakClient {

    @POST
    @Path("/realm/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateRealm(@PathParam("name") String name);

    @POST
    @Path("/federation/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateFederation(@PathParam("name") String name);

    @POST
    @Path("/client/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateClient(@PathParam("name") String name);
}

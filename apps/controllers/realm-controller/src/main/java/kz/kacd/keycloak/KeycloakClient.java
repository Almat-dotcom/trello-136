package kz.kacd.keycloak;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/k8s-config")
@Produces(MediaType.APPLICATION_JSON)            // ← сервер всё равно отдаёт JSON
public interface KeycloakClient {

    @POST
    @Path("/realm/{name}")
    Response updateRealm(@PathParam("name") String name);

    @POST
    @Path("/federation/{name}")
    Response updateFederation(@PathParam("name") String name);

    @POST
    @Path("/client/{name}")
    Response updateClient(@PathParam("name") String name);
}
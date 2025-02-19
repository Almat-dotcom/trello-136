package kz.kacd.sso.external.resource.logininfo;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.HashMap;
import java.util.Map;

public class LastLoginResource extends BaseAdminResource {

    private final KeycloakSession session;
    private final LastLoginService service;


    protected LastLoginResource(KeycloakSession session, RealmModel realm) {
        super(realm);
        this.session = session;
        this.service = new LastLoginService(session);
    }

    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastLogin(@PathParam("userId") String userId) {
        auth.requireManageUsers();
        Map<String, String> result = service.getLastLogin(userId);
        return Response.ok(result).build();
    }

}
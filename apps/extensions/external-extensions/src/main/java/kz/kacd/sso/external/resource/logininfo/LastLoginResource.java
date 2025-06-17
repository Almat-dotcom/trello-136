package kz.kacd.sso.external.resource.logininfo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.util.Map;

public class LastLoginResource extends BaseAdminResource {

    private final LastLoginService service;

    public LastLoginResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
        this.service = new LastLoginService(session);
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastLogin(@PathParam("userId") String userId) {
        Map<String, String> result = service.getLastLoginInfo(userId);

        if (result.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.ok(result).build();
    }
}
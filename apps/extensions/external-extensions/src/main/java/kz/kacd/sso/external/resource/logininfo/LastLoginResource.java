package kz.kacd.sso.external.resource.logininfo;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.HashMap;
import java.util.Map;

public class LastLoginResource implements RealmResourceProvider {

    private final KeycloakSession session;

    public LastLoginResource(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return this;
    }

    /**
     * Пример запроса:
     * GET /realms/<realm>/last-login/{userId}
     */
    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastLogin(@PathParam("userId") String userId) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("User not found")
                           .build();
        }

        String lastLoginTime = user.getFirstAttribute("lastLoginTime");
        String lastLoginIP = user.getFirstAttribute("lastLoginIP");

        Map<String, String> result = new HashMap<>();
        result.put("userId", userId);
        result.put("lastLoginTime", lastLoginTime != null ? lastLoginTime : "N/A");
        result.put("lastLoginIP", lastLoginIP != null ? lastLoginIP : "N/A");

        return Response.ok(result).build();
    }

    @Override
    public void close() {

    }
}

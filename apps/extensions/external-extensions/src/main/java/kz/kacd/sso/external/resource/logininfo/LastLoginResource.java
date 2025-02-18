package kz.kacd.sso.external.resource.logininfo;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.HashMap;
import java.util.Map;

public class LastLoginResource extends BaseAdminResource {

    private static final Logger LOG = Logger.getLogger(LastLoginResource.class);
    private final KeycloakSession session;


    public LastLoginResource(RealmModel realm, KeycloakSession session) {
        super(realm);
        this.session = session;
    }

    /**
     * Пример запроса:
     * GET /realms/<realm>/last-login/{userId}
     */
    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastLogin(@PathParam("userId") String userId) {
        LOG.infof("Received request for last login info for userId: %s", userId);

        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);
        if (user == null) {
            LOG.errorf("User not found for userId: %s", userId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        String lastLoginTime = user.getFirstAttribute("lastLoginTime");
        String lastLoginIP = user.getFirstAttribute("lastLoginIP");

        LOG.debugf("Fetched lastLoginTime: %s and lastLoginIP: %s for userId: %s",
                lastLoginTime, lastLoginIP, userId);

        Map<String, String> result = new HashMap<>();
        result.put("userId", userId);
        result.put("lastLoginTime", lastLoginTime != null ? lastLoginTime : "N/A");
        result.put("lastLoginIP", lastLoginIP != null ? lastLoginIP : "N/A");

        LOG.infof("Returning last login info for userId: %s", userId);
        return Response.ok(result).build();
    }
}
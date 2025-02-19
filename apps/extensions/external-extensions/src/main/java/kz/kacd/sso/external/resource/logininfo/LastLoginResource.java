package kz.kacd.sso.external.resource.logininfo;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.HashMap;
import java.util.Map;

public class LastLoginResource extends BaseAdminResource {

    private static final Logger LOG = Logger.getLogger(LastLoginResource.class);
    private final KeycloakSession session;


    public LastLoginResource(RealmModel realm, KeycloakSession session) {
        super(realm);
        this.session = session;
    }


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

        String lastLoginDate = user.getFirstAttribute("date");
        String lastLoginTime = user.getFirstAttribute("time");
        String lastLoginIP = user.getFirstAttribute("IP");

        Map<String, String> result = new HashMap<>();
        result.put("userId", userId);
        result.put("date", lastLoginDate != null ? lastLoginDate : "N/A");
        result.put("time", lastLoginTime != null ? lastLoginTime : "N/A");
        result.put("IP", lastLoginIP != null ? lastLoginIP : "N/A");

        return Response.ok(result).build();
    }
}
package kz.kacd.sso.external.resource.id;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

/**
 * Resource to exchange universal id with Keycloak UUID.
 */
public class IdResource extends BaseAdminResource {
    private static final Logger log = Logger.getLogger(IdResource.class);

    public IdResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
    }

    @GET
    @Path("physical/{idn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPhysicalId(@PathParam("idn") String idn) {

        log.debugf("Getting physical id by idn %s ...", idn);

        return users.searchForUserByUserAttributeStream(
                        realm,
                        ExternalRegistrationPage.FIELD_IIN,
                        idn)
                .filter(u -> ExternalRegistrationPage.CLIENT_PHYSICAL
                        .equals(u.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE)))
                .findFirst()
                .map(u -> Response.ok(new IdResponse(u.getId()))
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("{}")
                        .build());
    }

    @GET
    @Path("legal/{idn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLegalId(@PathParam("idn") String idn) {

        log.debugf("Getting legal id by bin %s ...", idn);

        OrganizationModel org = orgs.getOrganizationByBin(realm, idn);
        if (org == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
        }
        return Response.ok(new IdResponse(org.getId()))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    public static final class IdResponse {
        private final String id;

        public IdResponse(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
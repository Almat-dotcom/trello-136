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
import org.keycloak.models.RealmModel;

/**
 * Resource to exchange universal id with keycloak UUID.
 */
public class IdResource extends BaseAdminResource {
    private static final Logger log = Logger.getLogger(IdResource.class);

    protected IdResource(RealmModel realm) {
        super(realm);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("physical/{idn}")
    public Response getPhysicalId(@PathParam("idn") String idn) {
        log.debugf("Getting physical id by idn %s", idn);
        return users.searchForUserByUserAttributeStream(
                        realm,
                        ExternalRegistrationPage.FIELD_IIN,
                        idn
                ).filter(it ->
                        ExternalRegistrationPage.CLIENT_PHYSICAL.equals(
                                it.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE)
                        )
                )
                .findFirst()
                .map(it -> Response.ok(new IdResponse(it.getId()), MediaType.APPLICATION_JSON_TYPE).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).entity("{}").build());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("legal/{idn}")
    public Response getLegalId(@PathParam("idn") String idn) {
        log.debugf("Getting legal id by idn %s ...", idn);
        OrganizationModel org = orgs.getOrganizationByBin(realm, idn);
        if (org == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{}").build();
        }
        return Response.ok(new IdResponse(org.getId()), MediaType.APPLICATION_JSON_TYPE).build();
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

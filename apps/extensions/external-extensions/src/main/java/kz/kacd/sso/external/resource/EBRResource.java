package kz.kacd.sso.external.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

/**
 * Establishing business relations resource
 */
public class EBRResource extends BaseAdminResource {
    private static final Logger log = Logger.getLogger(EBRResource.class);

    protected EBRResource(RealmModel realm) {
        super(realm);
    }

    @POST
    @Path("legal")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setLegalEBR(EBRRequest request) {
        auth.requireViewOrgs();
        auth.requireManageUsers();

        log.debugf("Adding EBR %b to org %s ...", request.sign, request.id);
        if (addEBRToOrg(request.id, request.sign)) {
            return Response.accepted("{}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private boolean addEBRToOrg(String orgId, boolean ebrStatus) {
        OrganizationModel org = orgs.getOrganizationById(realm, orgId);
        if (org == null) {
            return false;
        }

        org.getPositions().forEach(position ->
                position.getUser().setSingleAttribute(ExternalRegistrationPage.FIELD_HAS_EBR, Boolean.toString(ebrStatus))
        );
        return true;
    }

    @POST
    @Path("physical")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPhysicalEBR(EBRRequest request) {
        auth.requireManageUsers();

        log.debugf("Adding EBR %b to user %s ...", request.sign, request.id);
        if (addEBRToUser(request.id, request.sign)) {
            return Response.accepted("{}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private boolean addEBRToUser(String userId, boolean ebrStatus) {
        UserModel user = users.getUserById(realm, userId);
        if (user == null) {
            return false;
        }

        user.setSingleAttribute(ExternalRegistrationPage.FIELD_HAS_EBR, Boolean.toString(ebrStatus));
        return true;
    }


    public static final class EBRRequest {
        private String id;
        private boolean sign;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isSign() {
            return sign;
        }

        public void setSign(boolean sign) {
            this.sign = sign;
        }
    }
}

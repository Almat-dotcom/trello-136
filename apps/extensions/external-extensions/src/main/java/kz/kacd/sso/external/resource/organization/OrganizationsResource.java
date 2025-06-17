package kz.kacd.sso.external.resource.organization;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.representation.OrganizationRepresentation;
import kz.kacd.sso.external.resource.BaseAdminResource;
import kz.kacd.sso.external.resource.common.OrganizationResourceType;
import org.jboss.logging.Logger;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.util.stream.Stream;

public class OrganizationsResource extends BaseAdminResource {
    private static final Logger log = Logger.getLogger(OrganizationsResource.class);
    private static final int MAX_RESULT = 200;

    public OrganizationsResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Stream<OrganizationRepresentation> listOrgs(
            @QueryParam("first") Integer firstResult,
            @QueryParam("max") Integer maxResults) {

        checkViewPermissions();

        int first = firstResult != null ? firstResult : 0;
        int max = maxResults != null ? maxResults : MAX_RESULT;

        log.debugf("Getting all organizations from %d, limit %d …", first, max);

        return orgs.getOrganizations(realm, first, max)
                .map(OrganizationRepresentation::from);
    }

    @Path("{id}")
    public OrganizationResource getOrg(@PathParam("id") String id) {
        checkViewPermissions();

        OrganizationModel org = orgs.getOrganizationById(realm, id);
        if (org == null) {
            org = orgs.getOrganizationByBin(realm, id);
        }

        if (org != null && org.getRealm().getId().equals(realm.getId())) {
            return setupResource(new OrganizationResource(session, realm, org));
        }
        throw organizationNotFound(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrg(@Valid OrganizationRepresentation body) {
        checkEditPermissions();

        OrganizationModel newOrg = orgs.createOrganization(realm, user);
        newOrg.setBin(body.getBin());
        newOrg.setName(body.getName());
        newOrg.setDisplayName(body.getDisplayName());

        OrganizationRepresentation rep = OrganizationRepresentation.from(newOrg);

        adminEvent.resource(OrganizationResourceType.ORGANIZATION.name())
                .operation(OperationType.CREATE)
                .resourcePath(session.getContext().getUri(), rep.getId())
                .representation(rep)
                .success();

        return Response.created(
                session.getContext().getUri()
                        .getAbsolutePathBuilder()
                        .path(rep.getId())
                        .build()
        ).build();
    }
}

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
import org.keycloak.models.RealmModel;

public class OrganizationResource extends BaseAdminResource {
    private static final Logger log = Logger.getLogger(OrganizationResource.class);

    private final OrganizationModel model;

    protected OrganizationResource(RealmModel realm, OrganizationModel model) {
        super(realm);
        this.model = model;
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrg() {
        log.debugf("Getting org %s ...", model.getId());
        return Response.ok(OrganizationRepresentation.from(model)).build();
    }

    @PUT
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrg(@Valid OrganizationRepresentation body) {
        checkEditPermissions();

        log.debugf("Updating org %s ...", model.getId());
        model.setName(body.getName());
        model.setDisplayName(body.getDisplayName());
        if (body.getEnabled() != null && body.getEnabled() != model.enabled()) {
            if (Boolean.TRUE.equals(body.getEnabled())) {
                model.enable();
            } else {
                model.disable();
            }
        }

        OrganizationRepresentation rep = OrganizationRepresentation.from(model);

        adminEvent.resource(OrganizationResourceType.ORGANIZATION.name())
                .operation(OperationType.UPDATE)
                .resourcePath(session.getContext().getUri(), rep.getId())
                .representation(rep)
                .success();

        return Response.accepted(rep).build();
    }

    @DELETE
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOrg() {
        checkEditPermissions();

        log.infof("Deleting organization %s ...", model.getId());
        if (orgs.removeOrganization(realm, model.getId())) {
            adminEvent.resource(OrganizationResourceType.ORGANIZATION.name())
                    .resourcePath(session.getContext().getUri(), model.getId())
                    .operation(OperationType.DELETE)
                    .representation(model.getId())
                    .success();
        }

        return Response.accepted(model.getId()).build();
    }

    @Path("members")
    public MembersResource members() {
        checkViewPermissions();
        return setupResource(new MembersResource(realm, model));
    }

    @Path("clients")
    public ClientsResource clients() {
        checkViewPermissions();
        return setupResource(new ClientsResource(realm, model));
    }
}

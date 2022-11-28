package kz.kacd.sso.external.resource;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.representation.PositionRepresentation;
import kz.kacd.sso.external.resource.common.OrganizationResourceType;
import org.jboss.logging.Logger;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.RealmModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MemberResource extends BaseOrganizationAdminResource {
    private static final Logger log = Logger.getLogger(MemberResource.class);

    private final OrganizationModel org;
    private final PositionModel model;

    protected MemberResource(RealmModel realm, OrganizationModel org, PositionModel model) {
        super(realm);
        this.org = org;
        this.model = model;
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMember() {
        log.debugf("Getting member %s ...", model.getId());
        return Response.ok(PositionRepresentation.from(model)).build();
    }

    @PATCH
    @Path("confirm")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirm() {
        checkEditPermissions();

        org.confirmPosition(model);

        PositionRepresentation rep = PositionRepresentation.from(model);

        adminEvent.resource(OrganizationResourceType.ORGANIZATION_MEMBERSHIP.name())
                .resourcePath(session.getContext().getUri(), rep.getId())
                .operation(OperationType.UPDATE)
                .representation(rep)
                .success();

        return Response.accepted(rep).build();
    }

    @PATCH
    @Path("revoke")
    @Produces(MediaType.APPLICATION_JSON)
    public Response revoke() {
        checkEditPermissions();

        org.revokePosition(model);

        PositionRepresentation rep = PositionRepresentation.from(model);

        adminEvent.resource(OrganizationResourceType.ORGANIZATION_MEMBERSHIP.name())
                .resourcePath(session.getContext().getUri(), rep.getId())
                .operation(OperationType.UPDATE)
                .representation(rep)
                .success();

        return Response.accepted(rep).build();
    }
}

package kz.kacd.sso.external.resource;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.representation.PageRepresentation;
import kz.kacd.sso.external.representation.PositionRepresentation;
import kz.kacd.sso.external.representation.RequestMember;
import kz.kacd.sso.external.resource.common.OrganizationResourceType;
import org.jboss.logging.Logger;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Stream;

public class MembersResource extends BaseOrganizationAdminResource {
    private static final Logger log = Logger.getLogger(MembersResource.class);

    private final OrganizationModel model;

    protected MembersResource(RealmModel realm, OrganizationModel model) {
        super(realm);
        this.model = model;
    }

    @GET
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public PageRepresentation<PositionRepresentation> findAll(
            @QueryParam("from") Integer fromArg,
            @QueryParam("limit") Integer limitArg
    ) {
        checkViewPermissions();

        int from = 0;
        if (fromArg != null) {
            from = fromArg;
        }
        int limit = 100;
        if (limitArg != null) {
            limit = limitArg;
        }

        log.debugf("Finding members for organization %s ...", model.getId());
        return model.getPositions(from, limit).map(it -> PositionRepresentation.from(session, realm, it));
    }

    @Path("{id}")
    public MemberResource member(@PathParam("id") String id) {
        checkViewPermissions();

        log.debugf("Getting member %s ...", id);
        PositionModel position = model.getPosition(id);
        if (position == null) {
            throw positionNotFound(id);
        }

        return setupResource(new MemberResource(realm, model, position));
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response request(@Valid RequestMember member) {
        checkEditPermissions();

        log.debugf("Requesting new membership for organization %s with user %s ...", model.getId(), member.getUserId());
        UserModel user = session.users().getUserById(realm, member.getUserId());
        if (user == null) {
            throw userNotFound(member.getUserId());
        }

        PositionModel position = model.requestPosition(member.getPosition(), user);
        if (position == null) {
            throw positionNotFound(member.getPosition());
        }

        PositionRepresentation rep = PositionRepresentation.from(session, realm, position);

        adminEvent.resource(OrganizationResourceType.ORGANIZATION_MEMBERSHIP.name())
                .resourcePath(session.getContext().getUri(), rep.getId())
                .operation(OperationType.CREATE)
                .representation(rep)
                .success();

        return Response.created(
                session.getContext().getUri().getAbsolutePathBuilder().path(rep.getId()).build()
        ).build();
    }
}

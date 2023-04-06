package kz.kacd.sso.resource.profile;

import kz.kacd.sso.resource.common.Page;
import kz.kacd.sso.resource.common.ProfileResourceRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RealmProfileResource extends BaseProfileRealmResource {
    private static final Logger log = Logger.getLogger(RealmProfileResource.class);

    private static final int LIMIT = 10;

    protected RealmProfileResource(RealmModel realm) {
        super(realm);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProfiles(
            @QueryParam("page") Integer pageParam
    ) {
        hasReadPermission();

        int page = pageParam != null ? pageParam : 1;
        log.debugf("Reading profiles by page {} ...", page);

        long total = session.users().getUsersCount(realm);

        Stream<UserModel> content = session.users()
                .searchForUserStream(realm, "*", (page - 1) * LIMIT, LIMIT);

        List<ProfileResourceRepresentation> representations = content
                .map(it -> ProfileResourceRepresentation.of(session, realm, it))
                .collect(Collectors.toList());

        return Response.ok(
                new Page<>(
                        page,
                        representations.size(),
                        LIMIT,
                        (int) (total / LIMIT) + 1,
                        total,
                        representations
                )
        ).build();
    }
}

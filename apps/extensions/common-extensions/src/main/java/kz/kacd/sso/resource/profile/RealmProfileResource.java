package kz.kacd.sso.resource.profile;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import kz.kacd.sso.resource.common.Page;
import kz.kacd.sso.resource.common.ProfileResourceRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.*;
import org.keycloak.models.utils.ModelToRepresentation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class RealmProfileResource extends BaseProfileRealmResource {
    private static final Logger log = Logger.getLogger(RealmProfileResource.class);

    private static final int MAX_LIMIT = 50;
    private static final String SUCCESS = "{\"status\": \"success\"}";

    protected RealmProfileResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProfiles(
            @QueryParam("page") Integer pageParam,
            @QueryParam("limit") Integer limitParam
    ) {
        hasReadPermission();

        int page = pageParam != null ? pageParam : 1;
        int limit = limitParam != null && limitParam < MAX_LIMIT ? limitParam : MAX_LIMIT;
        log.debugf("Reading profiles by page {} ...", page);

        long total = session.users().getUsersCount(realm);

        Stream<UserModel> content = session.users()
                .searchForUserStream(realm, "*", (page - 1) * limit, limit);

        List<ProfileResourceRepresentation> representations = content
                .map(it -> ProfileResourceRepresentation.of(session, realm, it))
                .collect(Collectors.toList());

        return Response.ok(
                new Page<>(
                        page,
                        representations.size(),
                        limit,
                        (int) (total / limit) + 1,
                        total,
                        representations
                )
        ).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") String id) {
        log.info("Start read profile TEST");
        hasReadPermission();
        log.info("Start read profile TEST2");

        log.debugf("Getting profile by user id {} ...", id);
        UserModel user = session.users().getUserById(realm, id);
        if (user == null) {
            throw new NotFoundException("User not found!");
        }

        return Response.ok(ProfileResourceRepresentation.of(session, realm, user)).build();
    }

    @GET
    @Path("is-email-exist/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isEmailExist(@PathParam("email") String email) {
        hasReadPermission();

        log.debugf("Getting profile by user email {} ...", email);
        UserModel user = session.users().getUserByEmail(realm, email);

        return Response.ok(user != null).build();
    }

    @GET
    @Path("is-phone-exist/{phone}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isPhoneExist(@PathParam("phone") String phone) {
        hasReadPermission();

        log.debugf("Getting profile by user phone {} ...", phone);
        List<UserModel> users = session.users().searchForUserByUserAttributeStream(realm, "phoneNumber", phone).collect(Collectors.toList());

        return Response.ok(!users.isEmpty()).build();
    }

    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(@PathParam("id") String id, UpdateProfileRequest request) {
        hasUpdatePermission();
        checkRequest(request);

        log.debugf("Finding user by id {} ...", id);
        UserModel user = session.users().getUserById(realm, id);
        if (user == null) {
            throw new NotFoundException("User not found!");
        }

        if (request.getNewEmail() != null) {
            log.infof("Updating email for user %s ...", user.getUsername());
            user.setEmail(request.getNewEmail());
            user.setEmailVerified(false);
            user.addRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);

            adminEvent(user);
        }

        if (request.getNewPhoneNumber() != null) {
            log.infof("Updating phone number for user %s ...", user.getUsername());
            user.setSingleAttribute("phoneNumber", request.getNewPhoneNumber());

            adminEvent(user);
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getMiddleName() != null) {
            user.setSingleAttribute("middleName", request.getMiddleName());
        }
        if (
                request.getFirstName() != null
                        || request.getLastName() != null
                        || request.getMiddleName() != null
        ) {
            log.infof("Updated full name of user %s ...", user.getUsername());
            adminEvent(user);
        }

        return Response.accepted(SUCCESS).build();
    }

    @POST
    @Path("{id}/role/{clientId}/{roleName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRole(
            @PathParam("id") String id,
            @PathParam("clientId") String clientId,
            @PathParam("roleName") String roleName
    ) {
        hasUpdatePermission();

        UserModel user = findUser(id);
        RoleModel role = findRole(clientId, roleName);

        user.grantRole(role);
        adminEvent(user);

        return Response.accepted(SUCCESS).build();
    }

    @DELETE
    @Path("{id}/role/{clientId}/{roleName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response revokeRole(
            @PathParam("id") String id,
            @PathParam("clientId") String clientId,
            @PathParam("roleName") String roleName
    ) {
        hasUpdatePermission();

        UserModel user = findUser(id);
        RoleModel role = findRole(clientId, roleName);

        user.deleteRoleMapping(role);
        adminEvent(user);

        return Response.accepted(SUCCESS).build();
    }

    private RoleModel findRole(String clientId, String roleName) {
        RoleModel role;
        if (clientId.equals("realm")) {
            role = session.roles().getRealmRole(realm, roleName);
        } else {
            ClientModel client = session.clients().getClientByClientId(realm, clientId);
            if (client == null) {
                throw new NotFoundException("Client not found!");
            }
            role = client.getRole(roleName);
        }
        if (role == null) {
            throw new NotFoundException("Role not found!");
        }
        return role;
    }

    private UserModel findUser(String id) {
        UserModel user = session.users().getUserById(realm, id);
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
        return user;
    }

    private void adminEvent(UserModel user) {
        adminEvent.resource(ResourceType.USER)
                .resourcePath("users/" + user.getId())
                .operation(OperationType.UPDATE)
                .representation(ModelToRepresentation.toBriefRepresentation(user))
                .success();
    }

    private void checkRequest(UpdateProfileRequest request) {
        checkString(request.getNewEmail(), "Email cannot be empty!");
        checkPhone(request.getNewPhoneNumber());
    }

    private void checkPhone(String phone) {
        checkString(phone, "Phone number cannot be empty!");
        if (phone != null && !phone.matches("\\+\\d{11}")) {
            throw new BadRequestException("Phone should be in format +77777777777");
        }
    }

    private void checkString(String s, String message) {
        if (s != null && s.isEmpty()) {
            throw new BadRequestException(message);
        }
    }
}

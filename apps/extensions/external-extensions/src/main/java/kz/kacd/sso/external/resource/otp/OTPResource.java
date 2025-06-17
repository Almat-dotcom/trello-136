package kz.kacd.sso.external.resource.otp;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.representation.OTPRequest;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class OTPResource extends BaseAdminResource {

    private final OTPService otpService;

    public OTPResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
        this.otpService = new OTPService(session);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response enableOTP(OTPRequest request) {
        auth.requireManageUsers();
        otpService.enableOTP(request.userId);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("{userId}")
    public Response disableOTP(@PathParam("userId") String userId) {
        auth.requireManageUsers();
        otpService.disableOTP(userId);
        return Response.noContent().build();
    }

    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOTPStatus(@PathParam("userId") String userId) {
        auth.requireManageUsers();
        boolean isEnabled = otpService.isOTPEnabled(userId);
        return Response.ok(isEnabled).build();
    }
}

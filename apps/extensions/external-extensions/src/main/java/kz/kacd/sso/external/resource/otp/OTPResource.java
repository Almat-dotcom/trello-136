package kz.kacd.sso.external.resource.otp;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.jboss.logging.Logger;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;

public class OTPResource extends BaseAdminResource {

    private static final Logger log = Logger.getLogger(OTPResource.class);
    private final OTPService otpService;
    private final KeycloakSession session;

    protected OTPResource(KeycloakSession session, RealmModel realm) {
        super(realm);
        this.session = session;
        this.otpService = new OTPService(session);  // Инициализация OTPService
    }

    @POST
    @Path("enable")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response enableOTP(OTPRequest request) {
        auth.requireManageUsers(); // Проверка прав доступа

        try {
            otpService.enableOTP(request.userId);
            return Response.ok(new OTPResponse("OTP enabled successfully.")).build();
        } catch (Exception e) {
            log.error("Error enabling OTP: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new OTPResponse("Failed to enable OTP: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("disable")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response disableOTP(OTPRequest request) {
        auth.requireManageUsers();

        try {
            otpService.disableOTP(request.userId);
            return Response.ok(new OTPResponse("OTP disabled successfully.")).build();
        } catch (Exception e) {
            log.error("Error disabling OTP: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new OTPResponse("Failed to disable OTP: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("status/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOTPStatus(@PathParam("userId") String userId) {
        boolean isEnabled = otpService.isOTPEnabled(userId);
        return Response.ok(isEnabled).build();
    }


    public static class OTPRequest {
        public String userId;
    }

    public static class OTPResponse {
        public String message;

        public OTPResponse(String message) {
            this.message = message;
        }
    }
}
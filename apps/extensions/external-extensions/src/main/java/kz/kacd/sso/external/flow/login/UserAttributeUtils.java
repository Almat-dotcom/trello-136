package kz.kacd.sso.external.flow.login;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class UserAttributeUtils {

    private static final String OTP_ENABLED_ATTRIBUTE = "otp_enabled";

    public static boolean isOtpEnabled(UserModel user) {
        String otpEnabled = user.getFirstAttribute(OTP_ENABLED_ATTRIBUTE);
        return otpEnabled != null && otpEnabled.equalsIgnoreCase("true");
    }

    public static void setOtpEnabled(KeycloakSession session, UserModel user, boolean enabled) {
        user.setAttribute(OTP_ENABLED_ATTRIBUTE, java.util.Collections.singletonList(String.valueOf(enabled)));
    }
}
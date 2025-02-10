package kz.kacd.sso.external.resource.otp;

import org.jboss.logging.Logger;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import static kz.kacd.sso.external.resource.otp.OtpConstants.CONFIGURE_TOTP;
import static kz.kacd.sso.external.resource.otp.OtpConstants.OTP_TYPE;

public class OTPService {

    private static final Logger log = Logger.getLogger(OTPService.class);
    private final KeycloakSession session;

    public OTPService(KeycloakSession session) {
        this.session = session;
    }

    public void enableOTP(String userId) {
        UserModel user = getValidUser(userId);

        user.credentialManager().getStoredCredentialsStream()
                .filter(cm -> OTP_TYPE.equals(cm.getType()))
                .map(CredentialModel::getId)
                .forEach(user.credentialManager()::removeStoredCredentialById);

        user.addRequiredAction(CONFIGURE_TOTP);
    }

    public void disableOTP(String userId) {
        UserModel user = getValidUser(userId);

        user.credentialManager().getStoredCredentialsStream()
                .filter(cm -> OTP_TYPE.equals(cm.getType()))
                .map(CredentialModel::getId)
                .findFirst()
                .ifPresent(user.credentialManager()::removeStoredCredentialById);
    }

    public boolean isOTPEnabled(String userId) {
        UserModel user = getValidUser(userId);
        return user.credentialManager().getStoredCredentialsStream()
                .anyMatch(cm -> OTP_TYPE.equals(cm.getType()));
    }

    private UserModel getValidUser(String userId) {
        UserModel user = getUserById(userId);
        if (user == null) {
            log.errorf("User not found: %s", userId);
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        return user;
    }

    private UserModel getUserById(String userId) {
        RealmModel realm = session.getContext().getRealm();
        return session.users().getUserById(realm, userId);
    }
}
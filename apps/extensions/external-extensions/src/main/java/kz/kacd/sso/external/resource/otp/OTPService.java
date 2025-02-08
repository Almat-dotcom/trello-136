package kz.kacd.sso.external.resource.otp;

import org.jboss.logging.Logger;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.OTPCredentialModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OTPService {

    private static final Logger log = Logger.getLogger(OTPResource.class);
    private final KeycloakSession session;

    public OTPService(KeycloakSession session) {
        this.session = session;
    }

    public void enableOTP(String userId) {
        UserModel user = session.users().getUserById(session.getContext().getRealm(), userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        user.setSingleAttribute("otp_enabled", "true");
        //TODO: Сгенерировать секрет OTP и сохранить его (например, в атрибутах пользователя)
        //TODO:  (Опционально) Отправить секрет пользователю (например, по email)
    }

    public void disableOTP(String userId) {
        UserModel user = session.users().getUserById(session.getContext().getRealm(), userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        user.removeAttribute("otp_enabled");
        //TODO: Удалить секрет OTP
    }

    public boolean isOTPEnabled(String userId) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);
        if (user == null) {
            return false;
        }
        UserCredentialManager userCredentialManager = session.getProvider(UserCredentialManager.class);
        // или session.userCredentialManager() в зависимости от версии

        Stream<CredentialModel> credsStream =
                userCredentialManager.getStoredCredentialsByTypeStream(realm, user, OTPCredentialModel.TYPE);
        List<CredentialModel> otpCreds = credsStream.collect(Collectors.toList());
        // Смотрим, есть ли у пользователя хоть один cred с type="otp"
        log.info(otpCreds.toString());

        boolean enabled = !otpCreds.isEmpty();
        log.infof("User %s -> isOTPEnabled=%s, count of OTP creds=%d", userId, enabled, otpCreds.size());

        return enabled;
    }

    //TODO:  Метод для проверки OTP (например, checkOTP(String userId, String otp))
}
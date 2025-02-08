package kz.kacd.sso.external.resource.otp;

import org.jboss.logging.Logger;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

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
        log.info("Realm: "+session.getContext().getRealm());
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(session.getContext().getRealm(), userId);
        log.info("User: "+user);
        if (user == null) {
            return false;
        }
        Stream<CredentialModel> credsStream = session.getProvider(UserCredentialStore.class)
                .getStoredCredentialsStream(realm, user);
        credsStream.forEach(credential -> {
            log.infof("Credential Type: %s, ID: %s, UserLabel: %s",
                    credential.getType(), credential.getId(), credential.getUserLabel());
        });
        log.info("Attributes: "+user.getAttributes().toString());
        String enabled = user.getFirstAttribute("otp_enabled");
        log.info("Otp Status is "+enabled);
        return "true".equals(enabled);
    }

    //TODO:  Метод для проверки OTP (например, checkOTP(String userId, String otp))
}
package kz.kacd.sso.external.resource.otp;

import org.jboss.logging.Logger;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.OTPCredentialProvider;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.services.resources.admin.UserResource;

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
            log.warnf("User not found: %s", userId);
            return false;
        }

        Stream<CredentialModel> storedCredentialsStream = user.credentialManager().getStoredCredentialsStream();
        storedCredentialsStream.forEach(e->{
            log.info("Credentiaaaal: "+e.getType());
        });

//        log.infof("isTotpEnabled for userId=%s => %s (found %d OTP creds)", userId, enabled, list.size());
        return true;
    }

    //TODO:  Метод для проверки OTP (например, checkOTP(String userId, String otp))
}
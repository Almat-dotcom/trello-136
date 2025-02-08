package kz.kacd.sso.external.resource.otp;

import org.jboss.logging.Logger;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.OTPCredentialModel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class OTPService {

    private static final Logger log = Logger.getLogger(OTPResource.class);
    private final KeycloakSession session;


    public OTPService(KeycloakSession session) {
        this.session = session;
    }

    public void enableOTP(String userId) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        OTPCredentialModel newOtp = OTPCredentialModel.createFromPolicy(
                realm,                   // Получаем настройки из realm (число цифр, период, алгоритм)
                "MyTOTP"                // метка (label), как будет называться метод TOTP
                // null => Keycloak сам сгенерирует секрет
                // или HOTP, но чаще TOTP
                // число цифр (digits)
                // период (timeStepSec)
        );

        user.credentialManager().createStoredCredential(newOtp);
        //TODO: Сгенерировать секрет OTP и сохранить его (например, в атрибутах пользователя)
        //TODO:  (Опционально) Отправить секрет пользователю (например, по email)
    }

    public void disableOTP(String userId) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        AtomicReference<String> id= new AtomicReference<>();
        Stream<CredentialModel> storedCredentialsStream = user.credentialManager().getStoredCredentialsStream();
        storedCredentialsStream.forEach(e->{
            if(e.getType().equals("otp")){
                id.set(e.getId());
            }
        });
        if(id.get() != null) {
            user.credentialManager().removeStoredCredentialById(id.get());
        }
        //TODO: Удалить секрет OTP
    }

    public boolean isOTPEnabled(String userId) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);
        if (user == null) {
            log.warnf("User not found: %s", userId);
            return false;
        }
        AtomicBoolean otp_enabled= new AtomicBoolean(false);
        Stream<CredentialModel> storedCredentialsStream = user.credentialManager().getStoredCredentialsStream();
        storedCredentialsStream.forEach(e->{
            if(e.getType().equals("otp")){
                otp_enabled.set(true);
            }
        });
        return otp_enabled.get();
    }

    //TODO:  Метод для проверки OTP (например, checkOTP(String userId, String otp))
}
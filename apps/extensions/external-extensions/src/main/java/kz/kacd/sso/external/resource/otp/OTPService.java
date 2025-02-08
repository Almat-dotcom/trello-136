package kz.kacd.sso.external.resource.otp;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class OTPService {

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
        UserModel user = session.users().getUserById(session.getContext().getRealm(), userId);
        if (user == null) {
            return false;
        }
        String enabled = user.getFirstAttribute("otp_enabled");
        return "true".equals(enabled);
    }

    //TODO:  Метод для проверки OTP (например, checkOTP(String userId, String otp))
}
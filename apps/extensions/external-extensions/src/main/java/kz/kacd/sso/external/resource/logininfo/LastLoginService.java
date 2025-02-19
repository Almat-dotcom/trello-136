package kz.kacd.sso.external.resource.logininfo;

import kz.kacd.sso.external.resource.otp.OTPService;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.HashMap;
import java.util.Map;

public class LastLoginService {

    private static final Logger log = Logger.getLogger(OTPService.class);
    private final KeycloakSession session;

    public LastLoginService(KeycloakSession session) {
        this.session = session;
    }

    public Map<String, String> getLastLogin(String userId) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);

        Map<String, String> result = new HashMap<>();
        result.put("userId", userId);

        if (user != null) {
            String previousLoginDate = user.getFirstAttribute("previousLoginTime");
            String previousLoginIP = user.getFirstAttribute("previousLoginIP");

            result.put("date", previousLoginDate != null ? previousLoginDate : "N/A");
            result.put("ip", previousLoginIP != null ? previousLoginIP : "N/A");
        }

        return result;
    }
}

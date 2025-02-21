package kz.kacd.sso.external.resource.logininfo;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LastLoginService {
    private static final Logger log = Logger.getLogger(LastLoginService.class);

    private static final String PREVIOUS_LOGIN_TIME = "previousLoginTime";
    private static final String PREVIOUS_LOGIN_IP = "previousLoginIP";
    private static final String PREVIOUS_LOGIN_DEVICE = "previousLoginDevice";

    private final KeycloakSession session;

    public LastLoginService(KeycloakSession session) {
        this.session = session;
    }

    public Map<String, String> getLastLoginInfo(String userId) {
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, userId);

        if (Objects.isNull(user)) {
            log.errorf("User not found: %s", userId);
            return new HashMap<>();
        }

        Map<String, String> result = new HashMap<>();
        result.put("userId", userId);
        result.put("date", getAttributeOrDefault(user, PREVIOUS_LOGIN_TIME));
        result.put("ip", getAttributeOrDefault(user, PREVIOUS_LOGIN_IP));
        result.put("device", getAttributeOrDefault(user, PREVIOUS_LOGIN_DEVICE));

        return result;
    }

    private String getAttributeOrDefault(UserModel user, String attributeName) {
        String value = user.getFirstAttribute(attributeName);
        return value != null ? value : "N/A";
    }
}
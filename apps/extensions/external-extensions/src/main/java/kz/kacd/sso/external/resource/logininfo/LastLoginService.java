package kz.kacd.sso.external.resource.logininfo;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static kz.kacd.sso.external.flow.login.utils.LoginInfoUtils.*;

public class LastLoginService {
    private static final Logger log = Logger.getLogger(LastLoginService.class);

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
        result.put("os", getAttributeOrDefault(user, PREVIOUS_LOGIN_OS));
        result.put("browser", getAttributeOrDefault(user, PREVIOUS_LOGIN_BROWSER));

        return result;
    }

    private String getAttributeOrDefault(UserModel user, String attributeName) {
        String value = user.getFirstAttribute(attributeName);
        return Objects.nonNull(value) ? value : "N/A";
    }
}
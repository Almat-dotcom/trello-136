package kz.kacd.sso.resource.common;

import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resources.admin.AdminAuth;

/**
 * Authorization utils for organizations admin REST API.
 */
public class ConfigAdminAuth extends AdminAuth {

    public static final String REALM_CONFIG_ROLE = "realm-config";

    public ConfigAdminAuth(RealmModel realm, AccessToken token, UserModel user, ClientModel client) {
        super(realm, token, user, client);
    }

    public boolean hasRealmConfig() {
        return hasAppRole(getClient(), REALM_CONFIG_ROLE);
    }
}

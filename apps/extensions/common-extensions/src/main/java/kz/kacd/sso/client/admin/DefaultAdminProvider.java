package kz.kacd.sso.client.admin;

import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.secret.SecretValueProvider;
import org.jboss.logging.Logger;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.*;
import org.keycloak.services.managers.ClientManager;
import org.keycloak.services.managers.RealmManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultAdminProvider implements AdminClientProvider {
    private static final Logger log = Logger.getLogger(DefaultAdminProvider.class);

    private static final String ADMIN_CLIENT = "realm-admin";

    private final KeycloakSession session;

    public DefaultAdminProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public ClientModel configureAdminClient(RealmModel realm) {
        log.debugf("Configuring admin client for realm %s ...", realm.getName());

        ClientModel result = session.clients().getClientByClientId(realm, ADMIN_CLIENT);
        if (result != null) {
            return result;
        }

        result = session.clients().addClient(realm, ADMIN_CLIENT);
        result.setDescription("Root client to manage realm.");
        result.setProtocol("openid-connect");
        result.setPublicClient(false);
        result.setBearerOnly(false);
        result.setSecret(SecretGenerator.getInstance().randomString());
        result.setServiceAccountsEnabled(true);
        result.setEnabled(true);

        ClientManager manager = new ClientManager(new RealmManager(session));
        manager.enableServiceAccount(result);

        UserModel sa = session.users().getServiceAccount(result);

        ClientModel realmManagement = session.clients().getClientByClientId(realm, "realm-management");
        if (realmManagement == null) {
            return null;
        }
        RoleModel role = realmManagement.getRolesStream()
                .filter(it -> it.getName().equals(ADMIN_CLIENT))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find realm-management role!"));
        sa.grantRole(role);

        if (K8sConfig.ENABLED) {
            SecretValueProvider secrets = session.getProvider(SecretValueProvider.class);
            Map<String, String> data = new HashMap<>();
            data.put("client_id", result.getClientId());
            data.put("client_secret", result.getSecret());
            secrets.create(
                    realm.getName() + "-admin-client",
                    Collections.singletonMap("kz-kacd-sso-realm", realm.getName()),
                    data
            );
        } else {
            log.infof(
                    "Created admin client for %s with client_id=%s and client_scret=%s",
                    realm.getName(),
                    result.getClientId(),
                    result.getSecret()
            );
        }

        return result;
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

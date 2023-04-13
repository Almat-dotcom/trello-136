package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import org.keycloak.models.*;

public class DrscbAccountRoleMapper {

    private final KeycloakSession session;
    private final RealmModel realm;

    public DrscbAccountRoleMapper(KeycloakSession session) {
        this.session = session;
        realm = session.getContext().getRealm();
    }

    public void map(DrscbAccount source, UserModel targetUser) {
        if (source.getRoles() != null) {
            source.getRoles().forEach(role -> {
                ClientModel client = session.clients().getClientByClientId(realm, role.clientId());
                RoleModel targetRole = client.getRolesStream()
                        .filter(it -> it.getName().equals(role.roleName()))
                        .findAny()
                        .orElse(null);
                if (targetRole == null) {
                    return;
                }

                targetUser.grantRole(targetRole);
            });
        }
    }
}

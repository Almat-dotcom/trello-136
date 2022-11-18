package kz.kacd.sso.realmcontroller.keycloak.client.model;

import kz.kacd.sso.realmcontroller.keycloak.Realm;
import org.keycloak.representations.idm.RoleRepresentation;

public class RoleBuilder {

    private final RoleRepresentation target = new RoleRepresentation();

    public RoleBuilder(String name) {
        target.setName(name);
    }

    public static RoleBuilder restricted() {
        return new RoleBuilder(Realm.RESTRICTED_ROLE);
    }

    public RoleBuilder forClient(Client client) {
        target.setClientRole(true);
        target.setContainerId(client.id());
        return this;
    }

    public RoleBuilder withDescription(String description) {
        target.setDescription(description);
        return this;
    }

    public Role build() {
        return new Role(target);
    }
}

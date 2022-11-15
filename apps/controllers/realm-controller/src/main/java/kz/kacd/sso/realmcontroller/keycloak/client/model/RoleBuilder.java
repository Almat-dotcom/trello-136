package kz.kacd.sso.realmcontroller.keycloak.client.model;

import org.keycloak.representations.idm.RoleRepresentation;

public class RoleBuilder {
    public static final String RESTRICTED = "restricted-access";

    private final RoleRepresentation target = new RoleRepresentation();

    public RoleBuilder(String name) {
        target.setName(name);
    }

    public static RoleBuilder restricted() {
        return new RoleBuilder(RESTRICTED);
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

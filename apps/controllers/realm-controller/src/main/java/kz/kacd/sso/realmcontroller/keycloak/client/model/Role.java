package kz.kacd.sso.realmcontroller.keycloak.client.model;

import org.keycloak.representations.idm.RoleRepresentation;

public record Role(RoleRepresentation representation) {

    public String id() {
        return representation.getId();
    }

    public void setId(String id) {
        representation.setId(id);
    }

    public String name() {
        return representation.getName();
    }
}

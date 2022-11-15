package kz.kacd.sso.realmcontroller.keycloak.group.model;

import org.keycloak.representations.idm.GroupRepresentation;

public record Group(GroupRepresentation representation) {

    public String id() {
        return representation.getId();
    }

    public String name() {
        return representation.getName();
    }
}

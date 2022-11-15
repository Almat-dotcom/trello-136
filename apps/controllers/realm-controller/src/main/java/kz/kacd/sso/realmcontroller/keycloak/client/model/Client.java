package kz.kacd.sso.realmcontroller.keycloak.client.model;

import org.keycloak.representations.idm.ClientRepresentation;

public record Client(ClientRepresentation representation) {

    public static ClientBuilder newInstance() {
        return new ClientBuilder();
    }

    public ClientBuilder update() {
        return new ClientBuilder(representation);
    }

    public String id() {
        return representation.getId();
    }

    public void setId(String id) {
        representation.setId(id);
    }

    public String name() {
        return representation.getClientId();
    }
}

package kz.kacd.sso.external.representation;

import kz.kacd.sso.external.model.PositionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class PositionRepresentation {

    private String id;
    private String name;
    private String description;
    private ProfileResourceRepresentation user;
    private Boolean confirmed;

    public PositionRepresentation() {
    }

    private PositionRepresentation(String id, String name, String description, ProfileResourceRepresentation user, Boolean confirmed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.user = user;
        this.confirmed = confirmed;
    }

    public static PositionRepresentation from(KeycloakSession session, RealmModel realm, PositionModel source) {
        return new PositionRepresentation(
                source.getId(),
                source.getName(),
                source.getDescription(),
                ProfileResourceRepresentation.of(session, realm, source.getUser()),
                source.confirmed()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProfileResourceRepresentation getUser() {
        return user;
    }

    public void setUser(ProfileResourceRepresentation user) {
        this.user = user;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}

package kz.kacd.sso.external.representation;

import org.keycloak.models.ClientModel;

import java.util.ArrayList;
import java.util.List;

public class OrganizationClientRepresentation {

    private final String clientId;
    private final String description;
    private final boolean active;
    private final List<String> scopes;

    private OrganizationClientRepresentation(String clientId, String description, boolean active, List<String> scopes) {
        this.clientId = clientId;
        this.description = description;
        this.active = active;
        this.scopes = scopes;
    }

    public static OrganizationClientRepresentation of(ClientModel model) {
        return new OrganizationClientRepresentation(
                model.getClientId(),
                model.getDescription(),
                model.isEnabled(),
                new ArrayList<>(model.getClientScopes(false).keySet())
        );
    }

    public String getClientId() {
        return clientId;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public List<String> getScopes() {
        return scopes;
    }
}

package kz.kacd.sso.external.representation;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateClientCommand {

    @NotBlank
    private String clientId;
    @NotBlank
    private String description;
    @NotNull
    private List<String> scopes;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}

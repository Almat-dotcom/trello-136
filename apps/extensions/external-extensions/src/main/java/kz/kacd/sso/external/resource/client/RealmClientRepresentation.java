package kz.kacd.sso.external.resource.client;

public class RealmClientRepresentation {

    private final String clientId;
    private final String name;
    private final String description;

    public RealmClientRepresentation(String clientId, String name, String description) {
        this.clientId = clientId;
        this.name = name;
        this.description = description;
    }

    public String getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

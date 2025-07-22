package kz.kacd.sso.external.resource.client;

public class RealmClientRoleRepresentation {

    private final String name;
    private final String description;

    public RealmClientRoleRepresentation(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

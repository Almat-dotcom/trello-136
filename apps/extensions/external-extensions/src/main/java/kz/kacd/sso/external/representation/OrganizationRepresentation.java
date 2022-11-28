package kz.kacd.sso.external.representation;

import kz.kacd.sso.external.model.OrganizationModel;

import javax.validation.Valid;

public class OrganizationRepresentation {

    private @Valid String id;
    private @Valid String bin;
    private @Valid String name;
    private @Valid String displayName;
    private @Valid Boolean enabled;
    private @Valid String createdAt;
    private @Valid String updatedAt;

    public OrganizationRepresentation() {
    }

    private OrganizationRepresentation(
            String id,
            String name,
            String displayName,
            Boolean enabled,
            String createdAt,
            String updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OrganizationRepresentation from(OrganizationModel source) {
        return new OrganizationRepresentation(
                source.getId(),
                source.getName(),
                source.getDisplayName(),
                source.enabled(),
                source.getCreatedAt().toString(),
                source.getUpdatedAt().toString()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

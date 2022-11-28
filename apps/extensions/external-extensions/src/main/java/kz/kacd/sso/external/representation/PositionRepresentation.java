package kz.kacd.sso.external.representation;

import kz.kacd.sso.external.model.PositionModel;

public class PositionRepresentation {

    private String id;
    private String name;
    private String description;
    private String userId;
    private Boolean confirmed;

    public PositionRepresentation() {
    }

    private PositionRepresentation(String id, String name, String description, String userId, Boolean confirmed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.confirmed = confirmed;
    }

    public static PositionRepresentation from(PositionModel source) {
        return new PositionRepresentation(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getUser().getId(),
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}

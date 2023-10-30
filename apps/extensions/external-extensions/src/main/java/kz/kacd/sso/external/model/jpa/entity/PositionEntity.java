package kz.kacd.sso.external.model.jpa.entity;

import jakarta.persistence.*;

@NamedQuery(
        name = "PositionEntity.getPositionByName",
        query = "select p from PositionEntity p where p.name = :name"
)
@Entity
@Table(name = "ext_position")
public class PositionEntity {

    @Id
    @Column(name = "id", length = 36)
    @Access(AccessType.PROPERTY)
    protected String id;

    @Column(name = "name", length = 36, nullable = false)
    protected String name;

    @Column(name = "description")
    protected String description;

    public PositionEntity() {
    }

    public PositionEntity(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
}

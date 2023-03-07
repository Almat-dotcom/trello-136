package kz.kacd.sso.external.model.jpa.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NamedQuery(
        name = "OrganizationMemberEntity.getOrganizationMembershipsByUserId",
        query = "select m from OrganizationMemberEntity m where m.userId = :userId and m.organization.realmId = :realmId"
)
@Entity
@Table(name = "ext_organization_member")
public class OrganizationMemberEntity {

    @Id
    @Column(name = "id", length = 36)
    @Access(AccessType.PROPERTY)
    protected String id = UUID.randomUUID().toString();

    @Column(name = "user_id", nullable = false)
    protected String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    protected OrganizationEntity organization;

    @ManyToOne
    @JoinColumn(name = "position_id")
    protected PositionEntity position;

    @Column(name = "confirmed", nullable = false)
    protected Boolean confirmed = false;

    @Column(name = "created_at", nullable = false)
    protected LocalDateTime createdAt = LocalDateTime.now();

    public OrganizationMemberEntity() {
    }

    public OrganizationMemberEntity(String id, String userId, OrganizationEntity organization, PositionEntity position) {
        this.id = id;
        this.userId = userId;
        this.organization = organization;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public PositionEntity getPosition() {
        return position;
    }

    public void setPosition(PositionEntity position) {
        this.position = position;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

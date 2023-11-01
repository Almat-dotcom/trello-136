package kz.kacd.sso.external.model.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedQuery(
        name = "OrganizationEntity.getOrgByBin",
        query = "select o from OrganizationEntity o where o.realmId = :realmId and o.bin = :bin"
)
@NamedQuery(
        name = "OrganizationEntity.getOrgByRealmId",
        query = "select o from OrganizationEntity o where o.realmId = :realmId"
)
@NamedNativeQuery(
        name = "OrganizationEntity.nextBin",
        query = "select nextval('{h-schema}ext_nonresident_org_code')"
)
@Entity
@Table(
        name = "ext_organization",
        uniqueConstraints = {@UniqueConstraint(name = "ext_organization_bin_uk", columnNames = {"bin"})}
)
public class OrganizationEntity {

    /**
     * Relations are fetched only by id. So AccessType.PROPERTY reduces redundant SQLs.
     */
    @Id
    @Column(name = "id", length = 36)
    @Access(AccessType.PROPERTY)
    protected String id;

    @Column(name = "bin", length = 12)
    protected String bin;

    @Column(name = "name")
    protected String name;

    @Column(name = "display_name", length = 2000)
    protected String displayName;

    @Column(name = "realm_id", nullable = false)
    protected String realmId;

    @Column(name = "created_by", nullable = false)
    protected String createdBy;

    @Column(name = "created_at", nullable = false)
    protected LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "enabled", nullable = false)
    protected Boolean enabled = true;

    @OneToMany(mappedBy = "organization", cascade = {CascadeType.REMOVE})
    protected Set<OrganizationMemberEntity> members = new HashSet<>();

    public OrganizationEntity() {
    }

    public OrganizationEntity(String id, String realmId, String createdBy) {
        this.id = id;
        this.realmId = realmId;
        this.createdBy = createdBy;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public String getRealmId() {
        return realmId;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<OrganizationMemberEntity> getMembers() {
        return members;
    }

    public void setMembers(Set<OrganizationMemberEntity> members) {
        this.members = members;
    }
}

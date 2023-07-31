package kz.kacd.sso.external.model.jpa;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.jpa.entity.OrganizationEntity;
import kz.kacd.sso.external.model.jpa.entity.OrganizationMemberEntity;
import kz.kacd.sso.external.model.jpa.entity.PositionEntity;
import kz.kacd.sso.external.representation.PageRepresentation;
import org.jboss.logging.Logger;
import org.keycloak.models.*;
import org.keycloak.models.jpa.JpaModel;
import org.keycloak.models.utils.KeycloakModelUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrganizationAdapter implements OrganizationModel, JpaModel<OrganizationEntity> {
    private static final Logger log = Logger.getLogger(OrganizationAdapter.class);

    private static final String LK_SHELL = "lk-shell-front";
    private static final String CEO = "ceo";

    private final KeycloakSession keycloakSession;
    private final OrganizationEntity entity;
    private final AdaptersFactory adaptersFactory;
    private final EntityManager em;
    private final RealmModel realm;

    private UserModel createdBy;

    public OrganizationAdapter(
            KeycloakSession keycloakSession,
            OrganizationEntity entity,
            AdaptersFactory adaptersFactory,
            EntityManager em,
            RealmModel realm
    ) {
        this.keycloakSession = keycloakSession;
        this.entity = entity;
        this.adaptersFactory = adaptersFactory;
        this.em = em;
        this.realm = realm;
    }

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public String getBin() {
        return entity.getBin();
    }

    @Override
    public void setBin(String bin) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setBin(bin);
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public void setName(String name) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setName(name);
    }

    @Override
    public String getDisplayName() {
        return entity.getDisplayName();
    }

    @Override
    public void setDisplayName(String displayName) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setDisplayName(displayName);
    }

    @Override
    public boolean enabled() {
        return entity.getEnabled();
    }

    @Override
    public void enable() {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setEnabled(true);
    }

    @Override
    public void disable() {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setEnabled(false);
    }

    @Override
    public RealmModel getRealm() {
        return realm;
    }

    @Override
    public UserModel getCreatedBy() {
        if (createdBy != null) {
            return createdBy;
        }

        createdBy = keycloakSession.users().getUserById(realm, entity.getCreatedBy());
        return createdBy;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return entity.getCreatedAt();
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return entity.getUpdatedAt();
    }

    @Override
    public Stream<PositionModel> getPositions() {
        return entity.getMembers().stream().map(it -> adaptersFactory.create(keycloakSession, it, realm));
    }

    @Override
    public PageRepresentation<PositionModel> getPositions(String userId, int from, int limit) {
        String sql = "select o from OrganizationMemberEntity o where o.organization = :organization";
        if (userId != null) {
            sql += " and o.userId = :userId";
        }
        TypedQuery<OrganizationMemberEntity> query = em.createQuery(sql, OrganizationMemberEntity.class);
        query.setParameter("organization", entity);
        if (userId != null) {
            query.setParameter("userId", userId);
        }
        query.setFirstResult(from);
        query.setMaxResults(limit);
        List<PositionModel> result = query.getResultStream()
                .map(it -> adaptersFactory.create(keycloakSession, it, realm))
                .collect(Collectors.toList());
        return new PageRepresentation<>(
                getPositionsCount(userId),
                from,
                limit,
                result
        );
    }

    private long getPositionsCount(String userId) {
        String sql = "select count(o) from OrganizationMemberEntity o where o.organization = :organization";
        if (userId != null) {
            sql += " and userId = :userId";
        }
        Query query = em.createQuery(sql);
        query.setParameter("organization", entity);
        if (userId != null) {
            query.setParameter("userId", userId);
        }
        return (Long) query.getSingleResult();
    }

    @Override
    public PositionModel getPosition(UserModel user) {
        return entity.getMembers().stream().filter(it -> it.getUserId().equals(user.getId()))
                .map(it -> adaptersFactory.create(keycloakSession, it, realm))
                .findFirst()
                .orElse(null);
    }

    @Override
    public PositionModel requestPosition(String positionName, UserModel user) {
        log.debug("Requesting position " + positionName + " for user " + user.getId());
        PositionEntity position = findPosition(positionName);
        if (position == null) {
            log.warn("Wrong position name " + positionName);
            return null;
        }

        OrganizationMemberEntity member = new OrganizationMemberEntity(
                KeycloakModelUtils.generateId(),
                user.getId(),
                entity,
                position
        );
        em.persist(member);
        em.flush();
        entity.getMembers().add(member);

        return adaptersFactory.create(keycloakSession, member, realm);
    }

    private PositionEntity findPosition(String positionName) {
        TypedQuery<PositionEntity> query = em.createNamedQuery("PositionEntity.getPositionByName", PositionEntity.class);
        query.setParameter("name", positionName);
        return query.getResultStream().findFirst().orElse(null);
    }

    @Override
    public void confirmPosition(PositionModel position) {
        log.debug("Confirming position " + position.getId());
        setConfirmed(position, true);

        if (PositionModel.HEAD.equals(position.getName())) {
            grantCeo(position.getUser());
        }
    }

    private void grantCeo(UserModel user) {
        RoleModel role = getCeoRole();
        if (role == null || user.hasRole(role)) {
            return;
        }
        user.grantRole(role);
    }

    @Override
    public void revokePosition(PositionModel position) {
        log.debug("Revoking position " + position.getId());
        setConfirmed(position, false);

        if (PositionModel.HEAD.equals(position.getName())) {
            revokeCeo(position.getUser());
        }
    }

    private void setConfirmed(PositionModel position, boolean confirmed) {
        OrganizationMemberEntity member = em.find(OrganizationMemberEntity.class, position.getId());
        member.setConfirmed(confirmed);
    }

    @Override
    public void removePosition(PositionModel position) {
        log.debug("Removing position " + position.getId());
        em.remove(em.find(OrganizationMemberEntity.class, position.getId()));

        if (PositionModel.HEAD.equals(position.getName())) {
            revokeCeo(position.getUser());
        }
    }

    private void revokeCeo(UserModel user) {
        RoleModel role = getCeoRole();
        if (role == null || !user.hasRole(role)) {
            return;
        }
        user.deleteRoleMapping(role);
    }

    private RoleModel getCeoRole() {
        ClientModel client = keycloakSession.clients().getClientByClientId(realm, LK_SHELL);
        if (client == null) {
            return null;
        }
        return keycloakSession.roles().getClientRole(client, CEO);
    }

    @Override
    public OrganizationEntity getEntity() {
        return entity;
    }
}

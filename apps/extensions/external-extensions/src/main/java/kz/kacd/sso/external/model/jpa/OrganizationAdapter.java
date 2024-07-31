package kz.kacd.sso.external.model.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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

import java.time.LocalDateTime;
import java.util.Collections;
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
    private final OrganizationClientManager clientManager;

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
        clientManager = new OrganizationClientManager(keycloakSession, entity, realm);
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
    public PositionModel getHead() {
        List<PositionModel> pos = getPositions(PositionModel.HEAD, null, 0, 1).getContent();
        if (pos.isEmpty()) {
            return null;
        }
        return pos.get(0);
    }

    @Override
    public Stream<PositionModel> getPositions() {
        return entity.getMembers().stream().map(it -> adaptersFactory.create(keycloakSession, it, realm));
    }

    @Override
    public PageRepresentation<PositionModel> getPositions(String position, String userId, int from, int limit) {
        long count = getPositionsCount(position, userId);
        if (count <= 0) {
            return new PageRepresentation<>(0, from, limit, Collections.emptyList());
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrganizationMemberEntity> query = cb.createQuery(OrganizationMemberEntity.class);
        Root<OrganizationMemberEntity> root = query.from(OrganizationMemberEntity.class);

        Predicate predicate = createPositionsPredicate(position, userId, root, cb);

        List<PositionModel> result = em.createQuery(
                query.select(root).where(predicate).orderBy(cb.asc(root.get("createdAt")))
        ).setFirstResult(from).setMaxResults(limit).getResultList().stream().map(entity ->
                adaptersFactory.create(keycloakSession, entity, realm)
        ).collect(Collectors.toList());
        return new PageRepresentation<>(
                count,
                from,
                limit,
                result
        );
    }

    private long getPositionsCount(String position, String userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<OrganizationMemberEntity> root = query.from(OrganizationMemberEntity.class);

        Predicate condition = createPositionsPredicate(position, userId, root, cb);
        if (condition == null) {
            return -1;
        }

        return em.createQuery(query.select(cb.count(root)).where(condition)).getSingleResult();
    }

    private Predicate createPositionsPredicate(
            String position,
            String userId,
            Root<OrganizationMemberEntity> root,
            CriteriaBuilder cb
    ) {
        Predicate predicate = cb.equal(root.get("organization"), entity);
        if (position != null) {
            PositionEntity entity = findPosition(position);
            if (entity == null) {
                log.warn("Wrong position name " + position);
                return null;
            }
            predicate = cb.and(predicate, cb.equal(root.get("position"), entity));
        }

        if (userId != null) {
            predicate = cb.and(predicate, cb.equal(root.get("userId"), userId));
        }
        return predicate;
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

    @Override
    public List<ClientModel> getClients(Boolean active) {
        return clientManager.getClients(active);
    }

    @Override
    public ClientModel getClient(String clientId) {
        return clientManager.getClient(clientId);
    }

    @Override
    public String createClient(String clientId, String description, List<String> scopes) {
        return clientManager.createClient(clientId, description, scopes);
    }

    @Override
    public void updateClient(String clientId, String description, boolean active) {
        clientManager.updateClient(clientId, description, active);
    }

    @Override
    public String resetClientSecret(String clientId) {
        return clientManager.resetClientSecret(clientId);
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

package kz.kacd.sso.external.model.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.jpa.entity.OrganizationEntity;
import kz.kacd.sso.external.model.jpa.entity.OrganizationMemberEntity;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Stream;

public class JpaOrganizationProvider implements OrganizationProvider {
    private static final Logger log = Logger.getLogger(JpaOrganizationProvider.class);

    private static final String REALM_ID = "realmId";
    private static final String BIN = "bin";
    private static final String USER_ID = "userId";

    private final AdaptersFactory adapterFactory;
    private final KeycloakSession keycloakSession;
    private final EntityManager em;

    public JpaOrganizationProvider(
            AdaptersFactory adapterFactory,
            KeycloakSession keycloakSession,
            EntityManager em
    ) {
        this.adapterFactory = adapterFactory;
        this.keycloakSession = keycloakSession;
        this.em = em;
    }

    @Override
    public OrganizationModel createOrganization(RealmModel realm, UserModel createdBy) {
        log.debug("Creating new organization in realm " + realm.getName() + " by " + createdBy.getId());
        OrganizationEntity entity = new OrganizationEntity(
                KeycloakModelUtils.generateId(),
                realm.getId(),
                createdBy.getId()
        );
        em.persist(entity);
        em.flush();

        OrganizationModel org = adapterFactory.create(keycloakSession, entity, adapterFactory, em, realm);
        keycloakSession.getKeycloakSessionFactory().publish(createdEvent(org));

        PositionModel head = org.requestPosition(PositionModel.HEAD, createdBy);
        org.confirmPosition(head);

        return org;
    }

    @Override
    public OrganizationModel getOrganizationByBin(RealmModel realm, String bin) {
        log.debug("Finding organization by bin " + bin + " " + realm.getId());
        TypedQuery<OrganizationEntity> query = em.createNamedQuery(
                "OrganizationEntity.getOrgByBin",
                OrganizationEntity.class
        );
        query.setParameter(REALM_ID, realm.getId());
        query.setParameter(BIN, bin);

        Optional<OrganizationEntity> entity = query.getResultStream()
                .findFirst();
        return entity.map(it -> adapterFactory.create(keycloakSession, it, adapterFactory, em, realm)).orElse(null);
    }

    @Override
    public Stream<OrganizationModel> getUserOrganizations(RealmModel realm, UserModel user) {
        log.debug("Finding organizations for user " + user.getId() + ": " + realm.getName());
        TypedQuery<OrganizationMemberEntity> query = em.createNamedQuery(
                "OrganizationMemberEntity.getOrganizationMembershipsByUserId",
                OrganizationMemberEntity.class
        );
        query.setParameter(REALM_ID, realm.getId());
        query.setParameter(USER_ID, user.getId());

        return query.getResultStream()
                .map(it -> adapterFactory.create(keycloakSession, it.getOrganization(), adapterFactory, em, realm));
    }

    @Override
    public Stream<OrganizationModel> getOrganizations(RealmModel realm, Integer firstResult, Integer maxResult) {
        log.debug(
                "Searching organizations in realm "
                + realm.getName()
                + " start from "
                + firstResult
                + " end with "
                + maxResult
        );
        TypedQuery<OrganizationEntity> query = em.createNamedQuery(
                "OrganizationEntity.getOrgByRealmId",
                OrganizationEntity.class
        );
        query.setParameter(REALM_ID, realm.getId());

        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }
        if (maxResult != null) {
            query.setMaxResults(maxResult);
        }
        return query.getResultStream().map(it -> adapterFactory.create(keycloakSession, it, adapterFactory, em, realm));
    }

    @Override
    public boolean removeOrganization(RealmModel realm, String id) {
        log.debug("Removing organization " + id + " : " + realm.getName());
        OrganizationModel org = getOrganizationById(realm, id);
        if (org == null) {
            log.warn("Organization " + id + " in realm " + realm.getId() + " does not exists!");
            return false;
        }

        OrganizationEntity entity = em.find(OrganizationEntity.class, id);
        em.remove(entity);
        keycloakSession.getKeycloakSessionFactory().publish(removedEvent(realm, org));
        em.flush();
        return true;
    }

    @Override
    public String generateNonResidentOrganizationBin() {
        log.debug("Generating new bin for non-resident organization ...");
        Query query = em.createNamedQuery("OrganizationEntity.nextBin");
        Long id = ((BigInteger) query.getSingleResult()).longValue();
        return String.format("NR%010d", id);
    }

    @Override
    public OrganizationModel getOrganizationById(RealmModel realm, String id) {
        log.debug("Finding organization by id " + id + " in realm " + realm.getId());
        OrganizationEntity entity = em.find(OrganizationEntity.class, id);
        if (entity != null && entity.getRealmId().equals(realm.getId())) {
            return adapterFactory.create(keycloakSession, entity, adapterFactory, em, realm);
        }
        return null;
    }

    @Override
    public void close() {
        // Nothing to close
    }

    private OrganizationModel.OrganizationCreatedEvent createdEvent(OrganizationModel org) {
        return new OrganizationModel.OrganizationCreatedEvent() {
            @Override
            public OrganizationModel getOrganization() {
                return org;
            }

            @Override
            public KeycloakSession getKeycloakSession() {
                return keycloakSession;
            }

            @Override
            public RealmModel getRealm() {
                return org.getRealm();
            }
        };
    }

    private OrganizationModel.OrganizationRemovedEvent removedEvent(RealmModel realm, OrganizationModel org) {
        return new OrganizationModel.OrganizationRemovedEvent() {
            @Override
            public OrganizationModel getOrganization() {
                return org;
            }

            @Override
            public KeycloakSession getKeycloakSession() {
                return keycloakSession;
            }

            @Override
            public RealmModel getRealm() {
                return realm;
            }
        };
    }
}

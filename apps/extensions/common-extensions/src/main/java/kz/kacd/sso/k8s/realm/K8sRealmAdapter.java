package kz.kacd.sso.k8s.realm;

import kz.kacd.sso.k8s.realm.model.K8sRealmMessages;
import kz.kacd.sso.k8s.realm.repository.K8sRealmRepository;
import kz.kacd.sso.v1.Realm;
import kz.kacd.sso.v1.RealmSpec;
import kz.kacd.sso.v1.RealmStatus;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderEvent;

import java.time.LocalDateTime;

public class K8sRealmAdapter implements K8sRealm {
    private static final Logger log = Logger.getLogger(K8sRealmAdapter.class);

    private final KeycloakSession keycloakSession;
    private final K8sRealmRepository repository;

    private final String name;
    private final String sourceGeneration;
    private final RealmSpec spec;
    private RealmStatus status;

    public K8sRealmAdapter(KeycloakSession keycloakSession, K8sRealmRepository repository, Realm realm) {
        this.keycloakSession = keycloakSession;
        this.repository = repository;
        this.name = realm.getMetadata().getName();
        this.sourceGeneration = realm.getMetadata().getGeneration().toString();
        this.spec = realm.getSpec();
        this.status = realm.getStatus();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RealmSpec getSpec() {
        return spec;
    }

    @Override
    public RealmStatus getStatus() {
        return status;
    }

    @Override
    public void applying() {
        log.infof("Making k8s realm %s applying ...", getName());

        RealmStatus newStatus = new RealmStatus();
        newStatus.setState(RealmStatus.State.APPLYING);
        newStatus.setMessage(K8sRealmMessages.REALM_IS_APPLYING);
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(realmIsApplying(this));
    }

    private ProviderEvent realmIsApplying(K8sRealm realm) {
        return new K8sRealmApplyingEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sRealm getRealm() {
                return realm;
            }
        };
    }

    @Override
    public void applied() {
        log.infof("Making k8s realm %s applied ...", getName());

        RealmStatus newStatus = new RealmStatus();
        newStatus.setState(RealmStatus.State.APPLIED);
        newStatus.setMessage(K8sRealmMessages.REALM_APPLIED);
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(realmApplied(this));
    }

    private ProviderEvent realmApplied(K8sRealm realm) {
        return new K8sRealmAppliedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sRealm getRealm() {
                return realm;
            }
        };
    }

    @Override
    public void toBackOff(Throwable e) {
        log.infof("Making k8s realm %s failed with backoff ...", getName());

        RealmStatus newStatus = new RealmStatus();
        newStatus.setState(RealmStatus.State.BACK_OFF);
        newStatus.setMessage(K8sRealmMessages.error(e));
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());
        newStatus.setBackoffSeconds(STANDARD_BACKOFF);

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(realmFailedWithBackoff(this, e));
    }

    private ProviderEvent realmFailedWithBackoff(K8sRealm realm, Throwable e) {
        return new K8sRealmBackOffedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sRealm getRealm() {
                return realm;
            }

            @Override
            public Throwable getCause() {
                return e;
            }
        };
    }

    @Override
    public void failed(Throwable e) {
        log.warnf("K8s realm %s failed. %s", getName(), K8sRealmMessages.error(e));

        RealmStatus newStatus = new RealmStatus();
        newStatus.setState(RealmStatus.State.FAILED);
        newStatus.setMessage(K8sRealmMessages.error(e));
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());
        newStatus.setBackoffSeconds(-1L);

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(realmFailed(this, e));
    }

    private ProviderEvent realmFailed(K8sRealm realm, Throwable e) {
        return new K8sRealmFailedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sRealm getRealm() {
                return realm;
            }

            @Override
            public Throwable getCause() {
                return e;
            }
        };
    }
}

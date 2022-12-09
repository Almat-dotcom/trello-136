package kz.kacd.sso.k8s.federation;

import kz.kacd.sso.k8s.federation.model.K8sFederationMessages;
import kz.kacd.sso.k8s.federation.repository.K8sFederationRepository;
import kz.kacd.sso.v1.Federation;
import kz.kacd.sso.v1.FederationSpec;
import kz.kacd.sso.v1.FederationStatus;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderEvent;

import java.time.LocalDateTime;

public class K8sFederationAdapter implements K8sFederation {
    private static final Logger log = Logger.getLogger(K8sFederationAdapter.class);

    private final KeycloakSession keycloakSession;
    private final K8sFederationRepository repository;

    private final String name;
    private final String sourceGeneration;
    private final FederationSpec spec;
    private FederationStatus status;

    public K8sFederationAdapter(KeycloakSession session, K8sFederationRepository repository, Federation source) {
        this.keycloakSession = session;
        this.repository = repository;
        this.name = source.getMetadata().getName();
        this.sourceGeneration = source.getMetadata().getGeneration().toString();
        this.spec = source.getSpec();
        this.status = source.getStatus();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FederationSpec getSpec() {
        return spec;
    }

    @Override
    public FederationStatus getStatus() {
        return status;
    }

    @Override
    public void applying() {
        log.infof("Marking federation %s as applying ...", name);

        FederationStatus newStatus = new FederationStatus();
        newStatus.setState(FederationStatus.State.APPLYING);
        newStatus.setMessage(K8sFederationMessages.FEDERATION_APPLYING);
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(federationApplying(this));
    }

    private ProviderEvent federationApplying(K8sFederation federation) {
        return new K8sFederation.K8sFederationApplyingEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sFederation getFederation() {
                return federation;
            }
        };
    }

    @Override
    public void applied() {
        log.infof("Making federation %s as applied ...", getName());

        FederationStatus newStatus = new FederationStatus();
        newStatus.setState(FederationStatus.State.APPLIED);
        newStatus.setMessage(K8sFederationMessages.FEDERATION_APPLIED);
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(federationApplied(this));
    }

    private ProviderEvent federationApplied(K8sFederation federation) {
        return new K8sFederation.K8sFederationAppliedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sFederation getFederation() {
                return federation;
            }
        };
    }

    @Override
    public void waitingForRealm() {
        log.warnf("Federation %s is targeted to not existing realm. Waiting to realm ...", getName());

        FederationStatus newStatus = new FederationStatus();
        newStatus.setState(FederationStatus.State.WAITING_REALM);
        newStatus.setMessage(K8sFederationMessages.WAITING_FOR_REALM);
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());
        newStatus.setBackoffSeconds(STANDARD_BACKOFF);

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(federationDelayed(this));
    }

    private ProviderEvent federationDelayed(K8sFederation federation) {
        return new K8sFederation.K8sFederationDelayedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sFederation getFederation() {
                return federation;
            }
        };
    }

    @Override
    public void backoff(Throwable e) {
        log.infof("Federation %s is backoffed ...", getName());

        FederationStatus newStatus = new FederationStatus();
        newStatus.setState(FederationStatus.State.BACK_OFF);
        newStatus.setMessage(K8sFederationMessages.error(e));
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());
        newStatus.setBackoffSeconds(STANDARD_BACKOFF);

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(federationBackoffed(this, e));
    }

    private ProviderEvent federationBackoffed(K8sFederation federation, Throwable e) {
        return new K8sFederation.K8sFederationBackOffedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sFederation getFederation() {
                return federation;
            }

            @Override
            public Throwable getCause() {
                return e;
            }
        };
    }

    @Override
    public void failed(Throwable e) {
        log.warnf("Federation %s is failed: %s", getName(), K8sFederationMessages.error(e));

        FederationStatus newStatus = new FederationStatus();
        newStatus.setState(FederationStatus.State.FAILED);
        newStatus.setMessage(K8sFederationMessages.error(e));
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());
        newStatus.setBackoffSeconds(STANDARD_BACKOFF);

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(federationFailed(this, e));
    }

    private ProviderEvent federationFailed(K8sFederation federation, Throwable e) {
        return new K8sFederation.K8sFederationFailedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sFederation getFederation() {
                return federation;
            }

            @Override
            public Throwable getCause() {
                return e;
            }
        };
    }
}

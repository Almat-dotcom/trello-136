package kz.kacd.sso.k8s.client;

import kz.kacd.sso.k8s.client.model.K8sClientMessages;
import kz.kacd.sso.k8s.client.repository.K8sClientRepository;
import kz.kacd.sso.v1.Client;
import kz.kacd.sso.v1.ClientSpec;
import kz.kacd.sso.v1.ClientStatus;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderEvent;

import java.time.LocalDateTime;

public class K8sClientAdapter implements K8sClient {
    private static final Logger log = Logger.getLogger(K8sClientAdapter.class);

    private final KeycloakSession keycloakSession;
    private final K8sClientRepository repository;

    private final String name;
    private final String sourceGeneration;
    private final ClientSpec spec;
    private ClientStatus status;

    public K8sClientAdapter(KeycloakSession session, K8sClientRepository repository, Client source) {
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
    public ClientSpec getSpec() {
        return spec;
    }

    @Override
    public ClientStatus getStatus() {
        return status;
    }

    @Override
    public void applying() {
        log.infof("Marking client %s as applying ...", name);

        ClientStatus newStatus = new ClientStatus();
        newStatus.setState(ClientStatus.State.APPLYING);
        newStatus.setMessage(K8sClientMessages.CLIENT_APPLYING);
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(clientApplying(this));
    }

    private ProviderEvent clientApplying(K8sClient client) {
        return new K8sClientApplyingEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sClient getClient() {
                return client;
            }
        };
    }

    @Override
    public void applied() {
        log.infof("Making client %s as applied ...", getName());

        ClientStatus newStatus = new ClientStatus();
        newStatus.setState(ClientStatus.State.APPLIED);
        newStatus.setMessage(K8sClientMessages.CLIENT_APPLIED);
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(clientApplied(this));
    }

    private ProviderEvent clientApplied(K8sClient client) {
        return new K8sClientAppliedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sClient getClient() {
                return client;
            }
        };
    }

    @Override
    public void waitingForRealm() {
        log.warnf("Client %s is targeted to not existing realm. Waiting to realm ...", getName());

        ClientStatus newStatus = new ClientStatus();
        newStatus.setState(ClientStatus.State.WAITING_FOR_REALM);
        newStatus.setMessage(K8sClientMessages.WAITING_FOR_REALM);
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());
        newStatus.setBackoffSeconds(STANDARD_BACKOFF);

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(clientDelayed(this));
    }

    private ProviderEvent clientDelayed(K8sClient client) {
        return new K8sClientDelayedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sClient getClient() {
                return client;
            }
        };
    }

    @Override
    public void backoff(Throwable e) {
        log.infof("Client %s is backoffed ...", getName());

        ClientStatus newStatus = new ClientStatus();
        newStatus.setState(ClientStatus.State.BACKOFF);
        newStatus.setMessage(K8sClientMessages.error(e));
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());
        newStatus.setBackoffSeconds(STANDARD_BACKOFF);

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(clientBackoffed(this, e));
    }

    private ProviderEvent clientBackoffed(K8sClient client, Throwable e) {
        return new K8sClientBackOffedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sClient getClient() {
                return client;
            }

            @Override
            public Throwable getCause() {
                return e;
            }
        };
    }

    @Override
    public void failed(Throwable e) {
        log.warnf("Client %s is failed: %s", getName(), K8sClientMessages.error(e));

        ClientStatus newStatus = new ClientStatus();
        newStatus.setState(ClientStatus.State.FAILED);
        newStatus.setMessage(K8sClientMessages.error(e));
        newStatus.setGeneration(sourceGeneration);
        newStatus.setLastApplication(LocalDateTime.now().toString());
        newStatus.setBackoffSeconds(STANDARD_BACKOFF);

        repository.updateStatus(getName(), newStatus);

        this.status = newStatus;

        keycloakSession.getKeycloakSessionFactory().publish(clientFailed(this, e));
    }

    private ProviderEvent clientFailed(K8sClient client, Throwable e) {
        return new K8sClientFailedEvent() {
            @Override
            public KeycloakSessionFactory getFactory() {
                return keycloakSession.getKeycloakSessionFactory();
            }

            @Override
            public K8sClient getClient() {
                return client;
            }

            @Override
            public Throwable getCause() {
                return e;
            }
        };
    }
}

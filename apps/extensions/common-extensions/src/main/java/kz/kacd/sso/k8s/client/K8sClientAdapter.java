package kz.kacd.sso.k8s.client;

import kz.kacd.sso.k8s.client.model.K8sClientMessages;
import kz.kacd.sso.k8s.client.repository.IsolatedK8sClientRepository;
import kz.kacd.sso.k8s.realm.K8sRealm;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderEvent;

import java.time.LocalDateTime;

public class K8sClientAdapter implements K8sClient {
    private static final Logger log = Logger.getLogger(K8sClientAdapter.class);

    private final KeycloakSession keycloakSession;
    private final IsolatedK8sClientRepository repository;

    private final String name;
    private final String realm;
    private final String sourceGeneration;
    private final Object spec;
    private Object status;

    public K8sClientAdapter(KeycloakSession session, IsolatedK8sClientRepository repository, Object source) {
        this.keycloakSession = session;
        this.repository = repository;
        
        try {
            // Get metadata using reflection
            Object metadata = source.getClass().getMethod("getMetadata").invoke(source);
            this.name = (String) metadata.getClass().getMethod("getName").invoke(metadata);
            
            Object labels = metadata.getClass().getMethod("getLabels").invoke(metadata);
            this.realm = (String) labels.getClass().getMethod("get", Object.class).invoke(labels, "realm");
            
            Object generation = metadata.getClass().getMethod("getGeneration").invoke(metadata);
            this.sourceGeneration = generation.toString();
            
            this.spec = source.getClass().getMethod("getSpec").invoke(source);
            this.status = source.getClass().getMethod("getStatus").invoke(source);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract client data via reflection", e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRealm() {
        return realm;
    }

    @Override
    public Object getSpec() {
        return spec;
    }

    @Override
    public Object getStatus() {
        return status;
    }

    @Override
    public void applying() {
        log.infof("Marking client %s as applying ...", name);

        repository.updateStatus(getName(), "APPLYING", K8sClientMessages.CLIENT_APPLYING, 
            sourceGeneration, LocalDateTime.now().toString());

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

        repository.updateStatus(getName(), "APPLIED", K8sClientMessages.CLIENT_APPLIED, 
            sourceGeneration, LocalDateTime.now().toString());

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
        log.infof("Marking client %s as waiting for realm ...", getName());

        repository.updateStatus(getName(), "PENDING", K8sClientMessages.WAITING_FOR_REALM, 
            sourceGeneration, LocalDateTime.now().toString());

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
        log.infof("Marking client %s as backoff ...", getName());

        repository.updateStatus(getName(), "BACKOFF", K8sClientMessages.error(e), 
            sourceGeneration, LocalDateTime.now().toString());

        keycloakSession.getKeycloakSessionFactory().publish(clientBackOffed(this, e));
    }

    private ProviderEvent clientBackOffed(K8sClient client, Throwable e) {
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
        log.infof("Marking client %s as failed ...", getName());

        repository.updateStatus(getName(), "FAILED", K8sClientMessages.error(e), 
            sourceGeneration, LocalDateTime.now().toString());

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

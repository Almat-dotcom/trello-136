package kz.kacd.sso.k8s.client;

import kz.kacd.sso.k8s.client.repository.K8sClientRepository;
import kz.kacd.sso.k8s.realm.K8sRealm;
import kz.kacd.sso.v1.Client;
import kz.kacd.sso.v1.ClientSpec;
import kz.kacd.sso.v1.ClientStatus;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;

public class K8sClientAdapter implements K8sClient {
    private static final Logger log = Logger.getLogger(K8sClientAdapter.class);

    private final KeycloakSession keycloakSession;
    private final K8sClientRepository repository;

    private final String name;
    private final String realm;
    private final String sourceGeneration;
    private final ClientSpec spec;
    private ClientStatus status;

    public K8sClientAdapter(KeycloakSession session, K8sClientRepository repository, Client source) {
        this.keycloakSession = session;
        this.repository = repository;
        this.name = source.getMetadata().getName();
        this.realm = source.getMetadata().getLabels().get(K8sRealm.REALM_LABEL);
        this.sourceGeneration = source.getMetadata().getGeneration().toString();
        this.spec = source.getSpec();
        this.status = source.getStatus();
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
        // Упрощенная версия - не обновляем статус в Kubernetes
    }

    @Override
    public void applied() {
        log.infof("Marking client %s as applied ...", name);
        // Упрощенная версия - не обновляем статус в Kubernetes
    }

    @Override
    public void waitingForRealm() {
        log.infof("Marking client %s as waiting for realm ...", name);
        // Упрощенная версия - не обновляем статус в Kubernetes
    }

    @Override
    public void backoff(Throwable e) {
        log.infof("Marking client %s as backoff due to %s ...", name, e.getMessage());
        // Упрощенная версия - не обновляем статус в Kubernetes
    }

    @Override
    public void failed(Throwable e) {
        log.infof("Marking client %s as failed due to %s ...", name, e.getMessage());
        // Упрощенная версия - не обновляем статус в Kubernetes
    }
}

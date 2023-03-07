package kz.kacd.sso.k8s.client;

import kz.kacd.sso.k8s.client.repository.K8sClientRepository;
import kz.kacd.sso.v1.Client;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;

import java.util.stream.Stream;

public class K8sClientSpecProviderImpl implements K8sClientSpecProvider {
    private static final Logger log = Logger.getLogger(K8sClientSpecProviderImpl.class);

    private final KeycloakSession session;
    private final K8sClientRepository repository;

    public K8sClientSpecProviderImpl(KeycloakSession session, K8sClientRepository repository) {
        this.session = session;
        this.repository = repository;
    }

    @Override
    public K8sClient findByName(String name) {
        log.debugf("Finding client by name %s ...", name);

        Client result = repository.find(name);
        if (result == null) {
            return null;
        }

        return new K8sClientAdapter(session, repository, result);
    }

    @Override
    public Stream<K8sClient> findByRealm(String name) {
        return repository.findByRealm(name).stream().map(it -> new K8sClientAdapter(session, repository, it));
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

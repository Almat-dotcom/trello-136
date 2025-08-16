package kz.kacd.sso.k8s.client;

import kz.kacd.sso.k8s.client.repository.IsolatedK8sClientRepository;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;

import java.util.stream.Stream;

public class IsolatedK8sClientSpecProviderImpl implements K8sClientSpecProvider {
    private static final Logger log = Logger.getLogger(IsolatedK8sClientSpecProviderImpl.class);

    private final KeycloakSession session;
    private final IsolatedK8sClientRepository repository;

    public IsolatedK8sClientSpecProviderImpl(KeycloakSession session, IsolatedK8sClientRepository repository) {
        this.session = session;
        this.repository = repository;
    }

    @Override
    public K8sClient findByName(String name) {
        log.debugf("Finding client by name %s ...", name);

        Object result = repository.find(name);
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

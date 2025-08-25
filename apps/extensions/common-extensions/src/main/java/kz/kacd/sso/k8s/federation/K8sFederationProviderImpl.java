package kz.kacd.sso.k8s.federation;

import kz.kacd.sso.k8s.federation.repository.K8sFederationRepository;
import kz.kacd.sso.v1.Federation;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;

import java.util.stream.Stream;

public class K8sFederationProviderImpl implements K8sFederationProvider {
    private static final Logger log = Logger.getLogger(K8sFederationProviderImpl.class);

    private final KeycloakSession session;
    private final K8sFederationRepository repository;

    public K8sFederationProviderImpl(KeycloakSession session, K8sFederationRepository repository) {
        this.session = session;
        this.repository = repository;
    }

    @Override
    public K8sFederation findByName(String name) {
        log.debugf("Finding federation by name %s ...", name);

        Federation result = repository.find(name);
        if (result == null) {
            return null;
        }

        return new K8sFederationAdapter(session, repository, result);
    }

    @Override
    public Stream<K8sFederation> findByRealm(String name) {
        return repository.findByRealm(name).stream().map(it -> new K8sFederationAdapter(session, repository, it));
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

package kz.kacd.sso.k8s.realm;

import kz.kacd.sso.k8s.realm.repository.K8sRealmRepository;
import kz.kacd.sso.v1.Realm;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;

public class K8sRealmProviderImpl implements K8sRealmProvider {
    private static final Logger log = Logger.getLogger(K8sRealmProviderImpl.class);

    private final KeycloakSession session;
    private final K8sRealmRepository repository;

    public K8sRealmProviderImpl(KeycloakSession session, K8sRealmRepository repository) {
        this.session = session;
        this.repository = repository;
    }

    @Override
    public K8sRealm findSpec(String name) {
        log.debugf("Finding spec for realm %s ...", name);
        Realm spec = repository.find(name);
        if (spec == null) {
            return null;
        }

        return new K8sRealmAdapter(session, repository, spec);
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

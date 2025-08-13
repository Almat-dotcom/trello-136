package kz.kacd.sso.external.k8s.realm;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderFactory;

public class K8sRealmProviderFactory implements ProviderFactory<K8sRealmProvider> {
    private static final Logger log = Logger.getLogger(K8sRealmProviderFactory.class);

    @Override
    public K8sRealmProvider create(KeycloakSession session) {
        log.info("K8sRealmProviderFactory.create() called");
        return new K8sRealmProviderImpl();
    }

    @Override
    public void init(Config.Scope config) {
        log.info("K8sRealmProviderFactory.init() called");
        // Инициализация не требуется
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        log.info("K8sRealmProviderFactory.postInit() called");
        // Пост-инициализация не требуется
    }

    @Override
    public void close() {
        log.info("K8sRealmProviderFactory.close() called");
        // Закрытие не требуется
    }

    @Override
    public String getId() {
        log.info("K8sRealmProviderFactory.getId() called");
        return "external-k8s-realm-provider";
    }
}

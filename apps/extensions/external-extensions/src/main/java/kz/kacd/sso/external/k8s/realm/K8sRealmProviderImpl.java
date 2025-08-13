package kz.kacd.sso.external.k8s.realm;

import org.jboss.logging.Logger;

public class K8sRealmProviderImpl implements K8sRealmProvider {
    private static final Logger log = Logger.getLogger(K8sRealmProviderImpl.class);

    @Override
    public K8sRealm findSpec(String name) {
        log.infof("Looking for realm spec with name: %s", name);
        // Простая реализация - возвращаем null, так как это тестовая версия
        return null;
    }

    @Override
    public void close() {
        // Ничего не делаем при закрытии
    }
}

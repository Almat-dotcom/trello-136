package kz.kacd.sso.k8s.realm;

import org.jboss.logging.Logger;

public class DefaultK8sRealmProvider implements K8sRealmProvider {
    private static final Logger log = Logger.getLogger(DefaultK8sRealmProvider.class);

    @Override
    public K8sRealm findSpec(String name) {
        log.infof("Looking for realm: %s", name);
        // Временно возвращаем null - будет реализовано позже
        return null;
    }

    @Override
    public void close() {
        // Nothing to close
    }
}


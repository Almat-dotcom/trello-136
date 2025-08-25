package kz.kacd.sso.k8s.federation;

import org.jboss.logging.Logger;

public class DefaultK8sFederationProvider implements K8sFederationProvider {
    private static final Logger log = Logger.getLogger(DefaultK8sFederationProvider.class);

    @Override
    public K8sFederation findByName(String name) {
        log.infof("Looking for federation: %s", name);
        // Временно возвращаем null - будет реализовано позже
        return null;
    }

    @Override
    public void close() {
        // Nothing to close
    }
}


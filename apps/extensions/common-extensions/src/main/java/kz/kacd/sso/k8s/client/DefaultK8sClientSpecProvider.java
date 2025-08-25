package kz.kacd.sso.k8s.client;

import org.jboss.logging.Logger;

public class DefaultK8sClientSpecProvider implements K8sClientSpecProvider {
    private static final Logger log = Logger.getLogger(DefaultK8sClientSpecProvider.class);

    @Override
    public K8sClient findByName(String name) {
        log.infof("Looking for client: %s", name);
        // Временно возвращаем null - будет реализовано позже
        return null;
    }

    @Override
    public void close() {
        // Nothing to close
    }
}


package kz.kacd.sso.external.k8s.realm;

import org.jboss.logging.Logger;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class K8sRealmProviderSpi implements Spi {
    private static final Logger log = Logger.getLogger(K8sRealmProviderSpi.class);

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        log.info("K8sRealmProviderSpi.getName() called");
        return "k8s-realm-provider";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        log.info("K8sRealmProviderSpi.getProviderClass() called");
        return K8sRealmProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        log.info("K8sRealmProviderSpi.getProviderFactoryClass() called");
        return K8sRealmProviderFactory.class;
    }
}

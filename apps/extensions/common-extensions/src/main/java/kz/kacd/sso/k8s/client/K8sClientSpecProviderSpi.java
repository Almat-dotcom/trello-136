package kz.kacd.sso.k8s.client;

import com.google.auto.service.AutoService;
import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.federation.K8sFederationProviderFactory;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class K8sClientSpecProviderSpi implements Spi {
    private static final String SPI_NAME = "k8s-client-spec-provider";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return SPI_NAME;
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return K8sClientSpecProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<?>> getProviderFactoryClass() {
        return K8sClientSpecProviderFactory.class;
    }

    @Override
    public boolean isEnabled() {
        return K8sConfig.ENABLED;
    }
}

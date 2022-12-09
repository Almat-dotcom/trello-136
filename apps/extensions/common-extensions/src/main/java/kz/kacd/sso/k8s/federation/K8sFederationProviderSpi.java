package kz.kacd.sso.k8s.federation;

import com.google.auto.service.AutoService;
import kz.kacd.sso.k8s.K8sConfig;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class K8sFederationProviderSpi implements Spi {
    private static final String SPI_NAME = "k8s-federation-provider-spi";

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
        return K8sFederationProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<?>> getProviderFactoryClass() {
        return K8sFederationProviderFactory.class;
    }

    @Override
    public boolean isEnabled() {
        return K8sConfig.ENABLED;
    }
}

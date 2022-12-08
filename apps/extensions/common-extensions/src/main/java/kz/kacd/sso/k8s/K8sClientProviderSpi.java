package kz.kacd.sso.k8s;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class K8sClientProviderSpi implements Spi {
    private static final String SPI_NAME = "k8s-client-provider";

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
        return K8sClientProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory<?>> getProviderFactoryClass() {
        return K8sClientProviderFactory.class;
    }

    @Override
    public boolean isEnabled() {
        return K8sConfig.ENABLED;
    }
}

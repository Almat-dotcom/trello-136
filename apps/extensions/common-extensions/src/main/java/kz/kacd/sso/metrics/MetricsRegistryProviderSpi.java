package kz.kacd.sso.metrics;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class MetricsRegistryProviderSpi implements Spi {

    public static final String SPI_NAME = "metrics-registry-provider";

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
        return MetricsRegistryProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return MetricsRegistryProviderFactory.class;
    }
}

package kz.kacd.sso.client.config;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class ClientConfigurerSpi implements Spi {
    private static final String SPI_NAME = "client-configurer";

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
        return ClientConfigurer.class;
    }

    @Override
    public Class<? extends ProviderFactory<?>> getProviderFactoryClass() {
        return ClientConfigurerFactory.class;
    }
}

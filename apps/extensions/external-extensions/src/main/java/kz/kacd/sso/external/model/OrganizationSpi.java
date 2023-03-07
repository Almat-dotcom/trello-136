package kz.kacd.sso.external.model;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class OrganizationSpi implements Spi {
    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "organizationProvider";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return OrganizationProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return OrganizationProviderFactory.class;
    }
}

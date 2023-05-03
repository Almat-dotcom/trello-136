package kz.kacd.sso.external.bmg;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class MobilePhoneValidatorSpi implements Spi {

    public static final String SPI_NAME = "mobile-phone-validator";

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
        return MobilePhoneValidator.class;
    }

    @Override
    public Class<? extends ProviderFactory<?>> getProviderFactoryClass() {
        return MobilePhoneValidatorFactory.class;
    }
}

package kz.kacd.sso.external.sign;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.xmldsig.KncaXS;
import org.jboss.logging.Logger;

import java.security.Provider;
import java.security.Security;

public class SecurityProviderInitializer {
    private static final Logger log = Logger.getLogger(SecurityProviderInitializer.class);

    private SecurityProviderInitializer() {
    }

    public static void init() {
        log.debug("Initializing provider ...");
        log.debug("Checking current KalkanProvider ...");
        Provider provider = Security.getProvider(KalkanProvider.PROVIDER_NAME);
        if (provider == null) {
            log.debug("Provider is not initialized. Initializing ...");
            provider = new KalkanProvider();
            Security.addProvider(provider);
            KncaXS.loadXMLSecurity();
            log.infof("Provider initialized. Provider name %s.", provider.getName());
        } else {
            log.debug("Provider is already initialized!");
        }
    }
}

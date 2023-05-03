package kz.kacd.sso.external.bmg.mkb;

import com.google.auto.service.AutoService;
import kz.kacd.sso.external.bmg.MobilePhoneValidator;
import kz.kacd.sso.external.bmg.MobilePhoneValidatorFactory;
import okhttp3.OkHttpClient;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(MobilePhoneValidatorFactory.class)
public class MkbMobilePhoneValidatorFactory implements MobilePhoneValidatorFactory {

    public static final String PROVIDER_ID = "mkb-mobile-phone-validator";

    @Override
    public MobilePhoneValidator create(KeycloakSession session) {
        OkHttpClient client = new OkHttpClient();
        KeycloakSelfClient selfClient = new KeycloakSelfClient(client);
        String mkbUrl = System.getenv("MKB_URL");
        MkbHttpClient mkbHttpClient = new MkbHttpClient(client, mkbUrl);
        return new MkbMobilePhoneValidator(session, selfClient, mkbHttpClient);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Nothing to do
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}

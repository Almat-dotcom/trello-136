package kz.kacd.sso.external.resource.otp;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class OTPResourceProviderFactory implements RealmResourceProviderFactory {
    public static final String PROVIDER_ID = "otp";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new OTPResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Можно использовать для чтения конфигурации из keycloak.conf
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Здесь можно зарегистрировать слушателей событий Keycloak, если нужно
    }

    @Override
    public void close() {
        // Освобождение ресурсов, если необходимо
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
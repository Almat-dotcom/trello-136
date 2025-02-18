package kz.kacd.sso.external.resource.logininfo;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class LastLoginResourceFactory implements RealmResourceProviderFactory {

    public static final String ID = "last-login";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new LastLoginResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Не требуется
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Не требуется
    }

    @Override
    public void close() {
        // Не требуется
    }

    @Override
    public String getId() {
        return ID;
    }
}

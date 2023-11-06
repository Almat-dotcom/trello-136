package kz.kacd.sso.external.resource.id;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class IdResourceProviderFactory implements RealmResourceProviderFactory {
    public static final String PROVIDER_ID = "id";

    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        return new IdResourceProvider(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        // Nothing to post init
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

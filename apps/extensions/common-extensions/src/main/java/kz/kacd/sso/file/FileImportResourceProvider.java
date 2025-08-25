package kz.kacd.sso.file;

import kz.kacd.sso.v1.Realm;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderEvent;

public interface FileImportResourceProvider extends Provider {

    void findRealmResources();

    interface RealmImportResourceFound extends ProviderEvent {
        KeycloakSessionFactory getSession();

        Realm getResource();
    }
}

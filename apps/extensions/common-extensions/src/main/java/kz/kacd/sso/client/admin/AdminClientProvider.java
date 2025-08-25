package kz.kacd.sso.client.admin;

import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface AdminClientProvider extends Provider {

    ClientModel configureAdminClient(RealmModel realm);
}

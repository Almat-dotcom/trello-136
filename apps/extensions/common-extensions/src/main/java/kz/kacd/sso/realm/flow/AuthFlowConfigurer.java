package kz.kacd.sso.realm.flow;

import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;

public interface AuthFlowConfigurer extends Provider {

    void configurePredefinedFlows(RealmModel realm);

    AuthenticationFlowModel findFlow(RealmModel realm, String alias);
}

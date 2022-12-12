package kz.kacd.sso.realm.flow;

import kz.kacd.sso.realm.flow.builder.ExternalBrowserBuilder;
import kz.kacd.sso.realm.flow.builder.ExternalRegistrationBuilder;
import kz.kacd.sso.realm.flow.builder.RestrictedBrowserBuilder;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowException;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.util.List;
import java.util.stream.Collectors;

import static kz.kacd.sso.realm.flow.AuthFlowConstants.*;

public class DefaultAuthFlowConfigurer implements AuthFlowConfigurer {
    private static final Logger log = Logger.getLogger(DefaultAuthFlowConfigurer.class);

    private final KeycloakSession session;

    public DefaultAuthFlowConfigurer(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void configurePredefinedFlows(RealmModel realm) {
        log.debugf("Configuring predefined flows for realm %s ...", realm.getName());

        if (realm.getFlowByAlias(RESTRICTED_BROWSER) == null) {
            new RestrictedBrowserBuilder(realm).build();
        }
        if (realm.getFlowByAlias(EXTERNAL_LOGIN) == null) {
            new ExternalBrowserBuilder(realm).build();
        }
        if (realm.getFlowByAlias(EXTERNAL_REGISTRATION) == null) {
            new ExternalRegistrationBuilder(realm).build();
        }
    }

    @Override
    public AuthenticationFlowModel findFlow(RealmModel realm, String alias) {
        return realm.getFlowByAlias(alias);
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

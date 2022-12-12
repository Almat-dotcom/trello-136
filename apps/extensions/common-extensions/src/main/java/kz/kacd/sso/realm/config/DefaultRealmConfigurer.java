package kz.kacd.sso.realm.config;

import kz.kacd.sso.k8s.realm.K8sRealm;
import kz.kacd.sso.realm.config.model.RealmConfigApplier;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderEvent;

public class DefaultRealmConfigurer implements KeycloakRealmConfigurer {
    private static final Logger log = Logger.getLogger(DefaultRealmConfigurer.class);

    private final KeycloakSession session;

    public DefaultRealmConfigurer(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public RealmModel configureWithDependencies(K8sRealm realm) {
        RealmModel result = configure(realm);

        session.getKeycloakSessionFactory().publish(realmConfigured(session, result));

        return result;
    }

    private ProviderEvent realmConfigured(KeycloakSession session, RealmModel realm) {
        return new KeycloakRealmConfigured() {
            @Override
            public KeycloakSession getSession() {
                return session;
            }

            @Override
            public RealmModel getRealm() {
                return realm;
            }
        };
    }

    @Override
    public RealmModel configure(K8sRealm realm) {
        log.infof("Configuring realm %s ...", realm.getName());
        realm.applying();

        RealmModel existing = find(realm);
        if (existing == null) {
            existing = create(realm);
        }

        new RealmConfigApplier(existing, session).apply(realm);

        // There is authorization flows

        return null;
    }

    private RealmModel find(K8sRealm realm) {
        return session.realms().getRealmByName(realm.getName());
    }

    private RealmModel create(K8sRealm realm) {
        return session.realms().createRealm(realm.getName());
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

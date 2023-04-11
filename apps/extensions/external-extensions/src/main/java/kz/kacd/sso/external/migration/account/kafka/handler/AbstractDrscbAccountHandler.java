package kz.kacd.sso.external.migration.account.kafka.handler;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public abstract class AbstractDrscbAccountHandler implements DrscbAccountHandler {

    protected final KeycloakSession session;
    protected final RealmModel realm;

    protected AbstractDrscbAccountHandler(KeycloakSession session) {
        this.session = session;
        realm = session.getContext().getRealm();
    }
}

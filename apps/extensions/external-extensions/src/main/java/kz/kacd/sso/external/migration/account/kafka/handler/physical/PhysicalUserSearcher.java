package kz.kacd.sso.external.migration.account.kafka.handler.physical;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class PhysicalUserSearcher {
    private static final Logger log = Logger.getLogger(PhysicalUserSearcher.class);

    private final KeycloakSession session;
    private final RealmModel realm;

    public PhysicalUserSearcher(KeycloakSession session) {
        this.session = session;
        realm = session.getContext().getRealm();
    }

    public UserModel find(DrscbAccount account) {
        log.debugf("Searching user for account {} ...", account.getId());
        if (account.isResident()) {
            return searchPhysicalResident(account);
        }
        return searchPhysicalNonResident(account);
    }

    private UserModel searchPhysicalResident(DrscbAccount account) {
        log.debugf("Searching physical resident account {} ...", account.getIin());
        return session.users()
                .searchForUserByUserAttributeStream(
                        realm,
                        ExternalRegistrationPage.FIELD_IIN,
                        account.getIin()
                )
                .filter(it ->
                        it.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE)
                                .equals(ExternalRegistrationPage.CLIENT_PHYSICAL)
                )
                .findAny()
                .orElse(null);
    }

    private UserModel searchPhysicalNonResident(DrscbAccount account) {
        if (!account.emailPresent()) {
            log.warnf("Unable to identify user account {}!", account.getId());
            return null;
        }

        log.debugf("Searching non-resident user account {} ...", account.getEmail());
        UserModel user = session.users().getUserByEmail(realm, account.getEmail());
        if (
                user != null
                        && user.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE)
                        .equals(ExternalRegistrationPage.CLIENT_PHYSICAL)
        ) {
            return user;
        }
        return null;
    }
}

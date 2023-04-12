package kz.kacd.sso.external.migration.account.kafka.handler.physical;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import static kz.kacd.sso.external.model.util.ExternalModelUtils.externalNonResidentUsername;
import static kz.kacd.sso.external.model.util.ExternalModelUtils.externalPhysicalResidentUsername;

public class PhysicalUserCreator {
    private static final Logger log = Logger.getLogger(PhysicalUserCreator.class);

    private final KeycloakSession session;
    private final RealmModel realm;

    public PhysicalUserCreator(KeycloakSession session) {
        this.session = session;
        this.realm = session.getContext().getRealm();
    }

    public UserModel create(DrscbAccount account) {
        log.debugf("Creating new user based on physical DRSCB account {} ...", account.getId());

        UserModel result;
        if (account.isResident()) {
            String username = externalPhysicalResidentUsername(account.getIin());
            log.infof("Creating new user {} for drscb account {} ...", username, account.getId());
            result = session.users().addUser(realm, username);
        } else {
            String email = account.getEmail();
            if (!account.emailPresent()) {
                log.warnf(
                        "Account {} will be created only for admin console. This account can't be identified!",
                        account.getId()
                );
            }
            result = session.users().addUser(realm, externalNonResidentUsername(email));
        }
        result.setSingleAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_PHYSICAL);

        return result;
    }
}

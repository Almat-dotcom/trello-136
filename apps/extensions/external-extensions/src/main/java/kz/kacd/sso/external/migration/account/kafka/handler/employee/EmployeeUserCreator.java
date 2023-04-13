package kz.kacd.sso.external.migration.account.kafka.handler.employee;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import static kz.kacd.sso.external.model.util.ExternalModelUtils.externalLegalResidentUsername;
import static kz.kacd.sso.external.model.util.ExternalModelUtils.externalNonResidentUsername;

public class EmployeeUserCreator {
    private static final Logger log = Logger.getLogger(EmployeeUserCreator.class);

    private final KeycloakSession session;
    private final RealmModel realm;

    public EmployeeUserCreator(KeycloakSession session) {
        this.session = session;
        this.realm = session.getContext().getRealm();
    }

    public UserModel create(DrscbAccount account) {
        log.debugf("Creating new employee {} ...", account.getId());
        OrganizationModel org = session.getProvider(OrganizationProvider.class)
                .getOrganizationByBin(realm, account.getBin());
        // We can't identify legal role of the account
        // if organization has not been registered by its head
        if (org == null) {
            log.infof("Skipping employee account of DRSCB account {} ...", account.getId());
            return null;
        }

        String username;
        if (account.isResident()) {
            username = externalLegalResidentUsername(account.getIin());
        } else {
            username = externalNonResidentUsername(account.getEmail());
        }
        UserModel user = session.users().addUser(realm, username);
        user.setSingleAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_LEGAL);
        org.requestPosition(PositionModel.EMPLOYEE, user);

        return user;
    }
}

package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.requiredaction.email.ChangeEmailRequiredAction;
import kz.kacd.sso.external.requiredaction.phone.ChangePhoneRequiredAction;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class AuthenticationDetailsHandler {
    private static final Logger log = Logger.getLogger(AuthenticationDetailsHandler.class);

    private final KeycloakSession session;
    private final RealmModel realm;

    public AuthenticationDetailsHandler(KeycloakSession session) {
        this.session = session;
        this.realm = session.getContext().getRealm();
    }

    public void applyAuthenticationDetails(DrscbAccount source, UserModel targetUser) {
        log.debugf("Applying authentication details on new account {} ...", source.getId());

        targetUser.addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);

        if (source.isResident()) {
            targetUser.setEmail(mockEmail(source.getId()));
            targetUser.addRequiredAction(ChangeEmailRequiredAction.PROVIDER_ID);
            targetUser.addRequiredAction(ChangePhoneRequiredAction.PROVIDER_ID);
            return;
        }

        String email = source.getEmail();
        if (source.emailPresent() && session.users().getUserByEmail(realm, email) == null) {
            targetUser.addRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);
        } else if (source.emailPresent()) {
            email = mockEmail(source.getId());
            targetUser.addRequiredAction(ChangeEmailRequiredAction.PROVIDER_ID);
        } else {
            targetUser.addRequiredAction(ChangeEmailRequiredAction.PROVIDER_ID);
        }
        targetUser.setEmail(email);

        if (source.phoneNumberPresent()) {
            targetUser.setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER, source.getPhoneNumber());
        } else {
            targetUser.addRequiredAction(ChangePhoneRequiredAction.PROVIDER_ID);
        }
    }

    private String mockEmail(String id) {
        return id + "@" + DrscbAccount.MOCK_EMAIL;
    }
}

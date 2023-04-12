package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.requiredaction.email.ChangeEmailRequiredAction;
import kz.kacd.sso.external.requiredaction.phone.ChangePhoneRequiredAction;
import org.jboss.logging.Logger;
import org.keycloak.models.UserModel;

public class AuthenticationDetailsHandler {
    private static final Logger log = Logger.getLogger(AuthenticationDetailsHandler.class);

    public void applyAuthenticationDetails(DrscbAccount source, UserModel targetUser) {
        log.debugf("Applying authentication details on new account {} ...", source.getId());

        if (source.emailPresent()) {
            targetUser.setEmail(source.getEmail());
            targetUser.addRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);
        } else {
            targetUser.addRequiredAction(ChangeEmailRequiredAction.PROVIDER_ID);
        }

        if (source.phoneNumberPresent()) {
            targetUser.setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER, source.getPhoneNumber());
        } else {
            targetUser.addRequiredAction(ChangePhoneRequiredAction.PROVIDER_ID);
        }
    }
}

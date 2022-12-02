package kz.kacd.sso.external.flow.login.legal;

import kz.kacd.sso.external.flow.login.AuthenticatorValidator;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalLoginPage;
import kz.kacd.sso.external.model.page.ExternalMessages;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.events.Errors;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.List;
import java.util.stream.Collectors;

public class LegalUserValidator implements AuthenticatorValidator {
    private static final Logger log = Logger.getLogger(LegalUserValidator.class);

    @Override
    public Error validate(UserModel user, KeycloakSession session, AuthenticationFlowContext context) {
        log.debugf("Validating user %s ...", user.getUsername());
        if (!user.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE).equals(ExternalRegistrationPage.CLIENT_LEGAL)) {
            log.debugf("User %s is not legal.", user.getUsername());
            return null;
        }

        RealmModel realm = session.getContext().getRealm();
        OrganizationProvider orgs = session.getProvider(OrganizationProvider.class);

        OrganizationModel organization;

        String bin = context.getAuthenticationSession().getAuthNote(ExternalRegistrationPage.FIELD_BIN);
        if (bin == null || bin.isEmpty()) {
            List<OrganizationModel> list = orgs.getUserOrganizations(realm, user).collect(Collectors.toList());
            if (list.size() != 1) {
                organization = null;
            } else {
                organization = list.get(0);
            }
        } else {
            organization = orgs.getOrganizationByBin(realm, bin);
        }
        if (organization == null) {
            return new Error(Errors.INVALID_USER_CREDENTIALS, null);
        }
        context.getEvent().detail(ExternalLoginPage.ORG_ID, organization.getId());

        PositionModel position = organization.getPosition(user);
        if (position == null) {
            return new Error(Errors.INVALID_USER_CREDENTIALS, null);
        }
        context.getEvent().detail(ExternalLoginPage.POSITION_ID, position.getId());
        context.getEvent().detail(ExternalLoginPage.POSITION_NAME, position.getName());

        if (!position.confirmed()) {
            return new Error(ExternalMessages.REQUIRED_CONFIRM_OF_MEMBER, null);
        }

        return null;
    }
}

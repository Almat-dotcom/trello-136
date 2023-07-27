package kz.kacd.sso.external.flow.register;

import kz.kacd.sso.external.bmg.MobilePhoneValidator;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.model.profile.ExternalUserProfile;
import kz.kacd.sso.external.model.profile.ExternalUserProfileProvider;
import org.jboss.logging.Logger;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.events.Details;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.MultivaluedMap;

/**
 * This execution is aimed to create external user on registration.
 */
public class ExternalRegistrationCreation implements FormAction {
    private static final Logger log = Logger.getLogger(ExternalRegistrationCreation.class);

    /**
     * Before registration form is rendered adds initial attributes.
     * </>
     * External users requires some additional attributes to register.
     * It should be added to registration form.
     */
    @Override
    public void buildPage(FormContext context, LoginFormsProvider form) {
        ExternalRegistrationPage page = ExternalRegistrationPage.from(form);
        page.configure();
    }

    /**
     * Just adds standard user model attributes to registration event.
     */
    @Override
    public void validate(ValidationContext context) {
        log.debug("Validating form ...");
        registerProfile(context);
        context.success();
    }

    /**
     * Creates initial user profile.
     */
    @Override
    public void success(FormContext context) {
        log.debug("Creating new user profile ...");
        ExternalUserProfile profile = registerProfile(context);

        UserModel user = profile.create();
        user.setEnabled(true);

        if (
                profile.residency().equals(ExternalRegistrationPage.RESIDENT)
                        && profile.clientType().equals(ExternalRegistrationPage.CLIENT_PHYSICAL)
        ) {
            validatePhone(user, context.getSession());
        }

        context.setUser(user);

        context.getEvent().user(user);
        context.getEvent().success();
        context.newEvent().event(EventType.LOGIN);
        context.getEvent().client(context.getAuthenticationSession().getClient().getClientId())
                .detail(Details.REDIRECT_URI, context.getAuthenticationSession().getRedirectUri())
                .detail(Details.AUTH_METHOD, context.getAuthenticationSession().getProtocol());
        String authType = context.getAuthenticationSession().getAuthNote(Details.AUTH_TYPE);
        if (authType != null) {
            context.getEvent().detail(Details.AUTH_TYPE, authType);
        }
    }

    private ExternalUserProfile registerProfile(FormContext context) {
        return registerProfile(
                context.getHttpRequest().getDecodedFormParameters(),
                context.getSession(),
                context.getEvent(),
                context.getAuthenticationSession()
        );
    }

    private void registerProfile(ValidationContext context) {
        registerProfile(
                context.getHttpRequest().getDecodedFormParameters(),
                context.getSession(),
                context.getEvent(),
                context.getAuthenticationSession()
        );
    }

    private ExternalUserProfile registerProfile(
            MultivaluedMap<String, String> formData,
            KeycloakSession session,
            EventBuilder event,
            AuthenticationSessionModel auth
    ) {
        event.detail(Details.REGISTER_METHOD, "form");
        ExternalUserProfileProvider provider = new ExternalUserProfileProvider(session);
        ExternalUserProfile profile = provider.create(formData);
        profile.rewriteFromSession(auth);

        event.detail(UserModel.EMAIL, profile.email());
        event.detail(UserModel.USERNAME, profile.username());
        event.detail(UserModel.FIRST_NAME, profile.firstName());
        event.detail(UserModel.LAST_NAME, profile.lastName());
        event.detail(ExternalRegistrationPage.FIELD_RESIDENCY, profile.residency());
        event.detail(ExternalRegistrationPage.FIELD_CLIENT_TYPE, profile.clientType());
        event.detail(ExternalRegistrationPage.FIELD_LEGAL_ROLE, profile.legalRole());
        event.detail(ExternalRegistrationPage.FIELD_IIN, profile.iin());
        event.detail(ExternalRegistrationPage.FIELD_BIN, profile.bin());

        return profile;
    }

    private void validatePhone(UserModel user, KeycloakSession session) {
        MobilePhoneValidator validator = session.getProvider(MobilePhoneValidator.class);
        String iin = user.getFirstAttribute(ExternalRegistrationPage.FIELD_IIN);
        String phoneNumber = user.getFirstAttribute(ExternalRegistrationPage.FIELD_PHONE_NUMBER);
        boolean verified = validator.check(iin, phoneNumber);
        user.setSingleAttribute(ExternalRegistrationPage.FIELD_PHONE_VERIFIED, verified + "");
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No required action
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

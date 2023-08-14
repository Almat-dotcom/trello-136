package kz.kacd.sso.external.flow.login.eds;

import kz.kacd.sso.external.flow.login.AlternativeAuthenticator;
import kz.kacd.sso.external.model.page.ExternalLoginPage;
import kz.kacd.sso.external.model.page.ExternalMessages;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.model.profile.ExternalUserProfile;
import kz.kacd.sso.external.model.profile.ExternalUserProfileProvider;
import kz.kacd.sso.external.sign.SignatureSubject;
import kz.kacd.sso.external.sign.SignatureValidator;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Collections;

public class EdsAuthenticator extends UsernamePasswordForm implements AlternativeAuthenticator {
    private static final Logger log = Logger.getLogger(EdsAuthenticator.class);

    private final SignatureValidator signatureValidator;

    public EdsAuthenticator(SignatureValidator signatureValidator) {
        this.signatureValidator = signatureValidator;
    }

    @Override
    public boolean isConfiguredFor(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String type = formData.getFirst(ExternalLoginPage.AUTHENTICATION_TYPE);
        return type != null && type.equals(ExternalLoginPage.EDS_AUTHENTICATION);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        log.debug("Authenticating using electronic digital signature ...");
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String eds = formData.getFirst(ExternalLoginPage.EDS);
        if (eds == null || eds.isEmpty()) {
            failAuth(context, ExternalMessages.MISSING_EDS);
            return;
        }

        SignatureValidator.Result result = signatureValidator.validate(eds);
        switch (result.getType()) {
            case ERROR:
                failAuth(context, result.getMessage());
                return;
            case PHYSICAL:
            case LEGAL:
                if (!validateUser(context, result)) {
                    return;
                }
                break;
        }

        context.getEvent().detail("authentication_form_method", ExternalLoginPage.EDS_AUTHENTICATION);
        context.success();
    }

    private boolean validateUser(AuthenticationFlowContext context, SignatureValidator.Result sign) {
        MultivaluedMap<String, String> userData = context.getHttpRequest().getDecodedFormParameters();
        String username = getUsername(context.getSession(), sign);
        if (sign.getType().equals(SignatureValidator.Type.LEGAL)) {
            context.getEvent().detail(ExternalRegistrationPage.FIELD_BIN, Collections.singletonList(sign.getSubject().getBin()));
            context.getAuthenticationSession().setAuthNote(ExternalRegistrationPage.FIELD_BIN, sign.getSubject().getBin());
        }
        userData.put(AuthenticationManager.FORM_USERNAME, Collections.singletonList(username));
        boolean result = validateUser(context, userData);
        if (
                result
                        && ExternalRegistrationPage.CLIENT_LEGAL
                        .equals(context.getUser().getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE))
        ) {
            updateProfile(context, sign.getSubject());
        }
        return result;
    }

    private String getUsername(KeycloakSession session, SignatureValidator.Result sign) {
        if (SignatureValidator.Type.PHYSICAL.equals(sign.getType())) {
            return sign.getSubject().getIin() + "-" + ExternalRegistrationPage.CLIENT_PHYSICAL;
        }
        String oldUsername = sign.getSubject().getIin() + "-" + ExternalRegistrationPage.CLIENT_LEGAL;
        String newUsername = sign.getSubject().getIin() + "-" + sign.getSubject().getBin() + "-" + ExternalRegistrationPage.CLIENT_LEGAL;

        if (session.users().getUserByUsername(session.getContext().getRealm(), oldUsername) != null) {
            return oldUsername;
        }
        return newUsername;
    }

    private void updateProfile(AuthenticationFlowContext context, SignatureSubject subject) {
        ExternalUserProfileProvider provider = new ExternalUserProfileProvider(context.getSession());
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle(ExternalRegistrationPage.FIELD_CLIENT_TYPE, ExternalRegistrationPage.CLIENT_LEGAL);
        ExternalUserProfile profile = provider.create(params, context.getUser());
        profile.rewriteFromSubject(subject, context.getAuthenticationSession());

        profile.update();
    }

    private void failAuth(AuthenticationFlowContext context, String message) {
        boolean clearUser = !isUserAlreadySetBeforeUsernamePasswordAuth(context);
        if (clearUser) {
            context.clearUser();
        }
        Response challenge = challenge(context, message, ExternalLoginPage.EDS);
        context.challenge(challenge);
    }
}

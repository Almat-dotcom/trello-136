package kz.kacd.sso.realm.flow.builder;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.RealmModel;

import static kz.kacd.sso.realm.flow.AuthFlowConstants.*;

public class ExternalBrowserBuilder extends AbstractLoginBuilder {

    public ExternalBrowserBuilder(RealmModel realm) {
        super(realm);
    }

    @Override
    protected String getRootAlias() {
        return EXTERNAL_LOGIN;
    }

    @Override
    protected String getRootDescription() {
        return EXTERNAL_LOGIN_DESC;
    }

    @Override
    protected void addFormsExecutions(AuthenticationFlowModel restrictedForm, AuthenticationFlowModel standardForm) {
        AuthenticationExecutionModel prev = addEDSOrPasswordForm(restrictedForm, standardForm);
        AuthenticationExecutionModel totpExecution = addOTPForm(restrictedForm, prev);
        addSessionCountLimiter(restrictedForm, totpExecution);
    }

    private AuthenticationExecutionModel addEDSOrPasswordForm(AuthenticationFlowModel parent, AuthenticationFlowModel forms) {
        AuthenticationExecutionModel source = getExecs(forms).stream()
                .filter(it -> it.getAuthenticator() != null && it.getAuthenticator().equals(STANDARD_USERNAME_FORM))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find username execution!"));

        AuthenticationExecutionModel target = new AuthenticationExecutionModel();
        target.setRequirement(source.getRequirement());
        target.setAuthenticator(EDS_USERNAME_PASSWORD_FORM);
        target.setPriority(source.getPriority());
        target.setAuthenticatorConfig(source.getAuthenticatorConfig());
        target.setParentFlow(parent.getId());
        target.setAuthenticatorFlow(source.isAuthenticatorFlow());
        return realm.addAuthenticatorExecution(target);
    }

    private AuthenticationExecutionModel addOTPForm(
            AuthenticationFlowModel parent,
            AuthenticationExecutionModel prev
    ) {
        // Штатный аутентификатор Keycloak для TOTP
        AuthenticationExecutionModel otpExecution = new AuthenticationExecutionModel();
        otpExecution.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
        otpExecution.setPriority(prev.getPriority() + 1);
        otpExecution.setAuthenticator("auth-otp-form");
        otpExecution.setParentFlow(parent.getId());
        otpExecution.setAuthenticatorFlow(false);

        return realm.addAuthenticatorExecution(otpExecution);
    }
}

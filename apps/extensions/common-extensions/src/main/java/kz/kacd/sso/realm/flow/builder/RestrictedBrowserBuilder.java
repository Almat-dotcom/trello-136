package kz.kacd.sso.realm.flow.builder;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.RealmModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kz.kacd.sso.realm.flow.AuthFlowConstants.*;

public class RestrictedBrowserBuilder extends AbstractLoginBuilder {
    private static final String ACCESS_PROVIDER = "accessProviderId";
    private static final String CLIENT_ROLE = "client-role";
    private static final String MESSAGE = "restrictClientAuthErrorMessage";
    private static final String ACCESS_DENIED = "access-denied";

    public RestrictedBrowserBuilder(RealmModel realm) {
        super(realm);
    }

    @Override
    protected String getRootAlias() {
        return RESTRICTED_BROWSER;
    }

    @Override
    protected String getRootDescription() {
        return RESTRICTED_BROWSER_DESC;
    }

    @Override
    protected void addFormsExecutions(AuthenticationFlowModel restrictedForm, AuthenticationFlowModel standardForm) {
        AuthenticationExecutionModel start = addUsernameForm(restrictedForm, standardForm);
        start = addRestrictAccess(restrictedForm, start);
        addSessionCountLimiter(restrictedForm, start);
    }

    private AuthenticationExecutionModel addUsernameForm(AuthenticationFlowModel parent, AuthenticationFlowModel forms) {
        AuthenticationExecutionModel source = getExecs(forms).stream()
                .filter(it -> it.getAuthenticator() != null && it.getAuthenticator().equals(STANDARD_USERNAME_FORM))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find username execution!"));

        copy(source, parent, null);
        return source;
    }

    private AuthenticationExecutionModel addRestrictAccess(AuthenticationFlowModel parent, AuthenticationExecutionModel prev) {
        AuthenticationExecutionModel target = new AuthenticationExecutionModel();
        target.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
        target.setPriority(prev.getPriority() + 1);
        target.setAuthenticator(RESTRICT_CLIENT_ACCESS);
        target.setParentFlow(parent.getId());
        target.setAuthenticatorConfig(config(
                RESTRICT_CLIENT_CONFIG,
                ACCESS_PROVIDER, CLIENT_ROLE,
                MESSAGE, ACCESS_DENIED
        ));
        return realm.addAuthenticatorExecution(target);
    }
}

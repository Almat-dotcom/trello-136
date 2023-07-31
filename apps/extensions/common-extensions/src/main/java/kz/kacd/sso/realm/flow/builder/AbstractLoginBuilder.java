package kz.kacd.sso.realm.flow.builder;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.RealmModel;

import java.util.List;

import static kz.kacd.sso.realm.flow.AuthFlowConstants.*;

public abstract class AbstractLoginBuilder extends AbstractFlowBuilder {

    private static final String BEHAVIOR = "behavior";
    private static final String TERMINATE_OLD = "Terminate oldest session";
    private static final String SESSION_MESSAGE = "errorMessage";
    private static final String SESSION_DENIED = "too-many-sessions";
    private static final String USER_SESSION_LIMIT = "userRealmLimit";
    private static final String USER_CLIENT_LIMIT = "userClientLimit";

    protected AbstractLoginBuilder(RealmModel realm) {
        super(realm);
    }

    public void build() {
        AuthenticationFlowModel standard = getStandardBrowser();
        List<AuthenticationExecutionModel> standardExecs = getExecs(standard);
        AuthenticationFlowModel root = copyRoot(standard);

        addStandardAlternativeFlows(root, standardExecs);
        addLoginForms(root, standard, standardExecs);
    }

    private AuthenticationFlowModel getStandardBrowser() {
        return realm.getFlowByAlias(STANDARD_BROWSER);
    }

    private void addStandardAlternativeFlows(AuthenticationFlowModel root, List<AuthenticationExecutionModel> standard) {
        standard.stream()
                .filter(it ->
                        it.getAuthenticator() != null
                                && (it.getAuthenticator().equals(COOKIE)
                                || it.getAuthenticator().equals(KERBEROS)
                                || it.getAuthenticator().equals(IDENTITY_PROVIDER))
                ).findFirst().ifPresent(it -> this.copy(it, root, null));
    }

    private void addLoginForms(
            AuthenticationFlowModel root,
            AuthenticationFlowModel standard,
            List<AuthenticationExecutionModel> standardExecs
    ) {
        AuthenticationExecutionModel formsExec = standardExecs.stream()
                .filter(it -> it.isAuthenticatorFlow() && it.getParentFlow().equals(standard.getId()))
                .findFirst()
                .orElse(null);
        if (formsExec == null) {
            throw new IllegalStateException("Cannot find forms in standard browser flow!");
        }

        AuthenticationFlowModel source = realm.getAuthenticationFlowById(formsExec.getFlowId());
        AuthenticationFlowModel restrictedForm = new AuthenticationFlowModel();
        restrictedForm.setProviderId(source.getProviderId());
        restrictedForm.setAlias(root.getAlias() + " " + source.getAlias());
        restrictedForm.setBuiltIn(false);
        restrictedForm.setTopLevel(false);
        restrictedForm.setDescription(source.getDescription());
        restrictedForm = realm.addAuthenticationFlow(restrictedForm);
        copy(formsExec, root, restrictedForm);

        addFormsExecutions(restrictedForm, source);
    }

    protected void addSessionCountLimiter(AuthenticationFlowModel parent, AuthenticationExecutionModel prev) {
        AuthenticationExecutionModel target = new AuthenticationExecutionModel();
        target.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
        target.setPriority(prev.getPriority() + 1);
        target.setAuthenticator(SESSION_COUNT_LIMIT);
        target.setParentFlow(parent.getId());
        target.setAuthenticatorConfig(config(
                SESSION_LIMIT_CONFIG,
                BEHAVIOR, TERMINATE_OLD,
                SESSION_MESSAGE, SESSION_DENIED,
                USER_SESSION_LIMIT, "1",
                USER_CLIENT_LIMIT, "0"
        ));
        realm.addAuthenticatorExecution(target);
    }

    protected abstract void addFormsExecutions(AuthenticationFlowModel restrictedForm, AuthenticationFlowModel standardForm);
}

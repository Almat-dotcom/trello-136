package kz.kacd.sso.realm.flow.builder;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.RealmModel;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static kz.kacd.sso.realm.flow.AuthFlowConstants.*;

public class ExternalRegistrationBuilder extends AbstractFlowBuilder {

    public ExternalRegistrationBuilder(RealmModel realm) {
        super(realm);
    }

    @Override
    public void build() {
        AuthenticationFlowModel standard = getStandardRegistration();
        List<AuthenticationExecutionModel> execs = getExecs(standard);
        AuthenticationFlowModel root = copyRoot(standard);

        addRegistrationForms(root, standard, execs);
    }

    private AuthenticationFlowModel getStandardRegistration() {
        return realm.getFlowByAlias(STANDARD_REGISTRATION);
    }

    private void addRegistrationForms(
            AuthenticationFlowModel parent,
            AuthenticationFlowModel standard,
            List<AuthenticationExecutionModel> execs
    ) {
        AuthenticationExecutionModel formsExec = execs.stream()
                .filter(it -> it.isAuthenticatorFlow() && it.getParentFlow().equals(standard.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find registration forms!"));

        AuthenticationFlowModel source = realm.getAuthenticationFlowById(formsExec.getFlowId());
        AuthenticationFlowModel registrationForm = new AuthenticationFlowModel();
        registrationForm.setProviderId(source.getProviderId());
        registrationForm.setAlias(parent.getAlias() + " " + source.getAlias());
        registrationForm.setBuiltIn(false);
        registrationForm.setTopLevel(false);
        registrationForm.setDescription(source.getDescription());
        registrationForm = realm.addAuthenticationFlow(registrationForm);
        copy(formsExec, parent, registrationForm);

        addFormExecutions(registrationForm, source);
    }

    private void addFormExecutions(AuthenticationFlowModel parent, AuthenticationFlowModel source) {
        List<AuthenticationExecutionModel> execs = getExecs(source)
                .stream()
                .sorted(Comparator.comparingInt(AuthenticationExecutionModel::getPriority))
                .collect(Collectors.toList());

        addUserCreation(parent, execs.get(0));
        addProfileValidation(parent, execs.get(1));
        copy(execs.get(2), parent, null);
        copy(execs.get(3), parent, null);
    }

    private void addUserCreation(AuthenticationFlowModel parent, AuthenticationExecutionModel source) {
        addExecution(parent, source, EXTERNAL_USER_CREATION);
    }

    private void addProfileValidation(AuthenticationFlowModel parent, AuthenticationExecutionModel source) {
        addExecution(parent, source, EXTERNAL_PROFILE);
    }

    private void addExecution(AuthenticationFlowModel parent, AuthenticationExecutionModel source, String authenticator) {
        AuthenticationExecutionModel target = new AuthenticationExecutionModel();
        target.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
        target.setAuthenticatorFlow(false);
        target.setAuthenticator(authenticator);
        target.setPriority(source.getPriority());
        target.setParentFlow(parent.getId());
        realm.addAuthenticatorExecution(target);
    }

    @Override
    protected String getRootAlias() {
        return EXTERNAL_REGISTRATION;
    }

    @Override
    protected String getRootDescription() {
        return EXTERNAL_REGISTRATION_DESC;
    }
}

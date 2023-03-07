package kz.kacd.sso.realm.flow.builder;

import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.RealmModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractFlowBuilder {

    protected final RealmModel realm;

    protected AbstractFlowBuilder(RealmModel realm) {
        this.realm = realm;
    }

    public abstract void build();

    protected AuthenticationFlowModel copyRoot(AuthenticationFlowModel source) {
        AuthenticationFlowModel model = new AuthenticationFlowModel();
        model.setAlias(getRootAlias());
        model.setDescription(getRootDescription());
        model.setBuiltIn(false);
        model.setTopLevel(true);
        model.setProviderId(source.getProviderId());

        return realm.addAuthenticationFlow(model);
    }

    protected abstract String getRootAlias();

    protected abstract String getRootDescription();

    protected void copy(AuthenticationExecutionModel source, AuthenticationFlowModel parent, AuthenticationFlowModel flow) {
        AuthenticationExecutionModel target = new AuthenticationExecutionModel();
        target.setRequirement(source.getRequirement());
        target.setAuthenticator(source.getAuthenticator());
        target.setPriority(source.getPriority());
        target.setAuthenticatorConfig(source.getAuthenticatorConfig());
        target.setParentFlow(parent.getId());
        target.setAuthenticatorFlow(source.isAuthenticatorFlow());
        if (flow != null) {
            target.setAuthenticatorFlow(true);
            target.setFlowId(flow.getId());
        }
        realm.addAuthenticatorExecution(target);
    }

    protected String config(String alias, String... args) {
        Map<String, String> source = toMap(args);
        AuthenticatorConfigModel result = new AuthenticatorConfigModel();
        result.setAlias(alias);
        result.setConfig(source);
        return realm.addAuthenticatorConfig(result).getId();
    }

    protected Map<String, String> toMap(String[] array) {
        Map<String, String> result = new HashMap<>();
        String key = null;
        for (int i = 0; i < array.length; i++) {
            if (i % 2 == 0) {
                key = array[i];
            } else {
                result.put(key, array[i]);
            }
        }
        return result;
    }

    protected List<AuthenticationExecutionModel> getExecs(AuthenticationFlowModel flow) {
        return realm.getAuthenticationExecutionsStream(flow.getId()).collect(Collectors.toList());
    }
}

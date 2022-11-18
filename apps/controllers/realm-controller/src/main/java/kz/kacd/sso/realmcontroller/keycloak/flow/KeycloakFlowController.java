package kz.kacd.sso.realmcontroller.keycloak.flow;

import kz.kacd.sso.realmcontroller.keycloak.Realm;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakFlowController {
    private static final String AUTHENTICATOR = "restrict-client-auth-authenticator";
    private static final Integer AUTHENTICATOR_PRIORITY = 31;
    private static final String ACCESS_PROVIDER_ID = "accessProviderId";
    private static final String CLIENT_ROLE_PROVIDER = "client-role";
    private static final String MESSAGE = "restrictClientAuthErrorMessage";
    private static final String MESSAGE_VALUE = "access-denied";

    private final KeycloakFlowRepository repository;

    public OperationResponse<Boolean> createRestrictedFlow(Realm realm) {
        log.debug("Creating restricted flow for realm {} ...", realm.name());
        var flowRes = repository.find(realm, Realm.RESTRICTED_AUTH_FLOW);
        if (flowRes instanceof OperationResponse.Success<AuthenticationFlowRepresentation>) {
            return OperationResponse.success(true);
        }

        if (flowRes instanceof OperationResponse.Failure<?> f) {
            if (f.getKind() == OperationResponse.K8sFailures.NOT_FOUND) {
                flowRes = repository.createCopy(realm, Realm.BROWSER_AUTH_FLOW, Realm.RESTRICTED_AUTH_FLOW);
            } else {
                return flowRes.map(it -> true);
            }
        }
        if (flowRes instanceof OperationResponse.Success<AuthenticationFlowRepresentation> s) {
            var flow = s.getData();

            var formsRes = repository.markFormsAsRequired(realm, flow);
            if (formsRes instanceof OperationResponse.Failure<Boolean>) {
                return formsRes;
            }

            var restrictionExecutor = new AuthenticationExecutionRepresentation();
            restrictionExecutor.setParentFlow(flow.getId());
            restrictionExecutor.setAuthenticatorFlow(false);
            restrictionExecutor.setAuthenticator(AUTHENTICATOR);
            restrictionExecutor.setPriority(AUTHENTICATOR_PRIORITY);
            restrictionExecutor.setRequirement(KeycloakFlowRepository.REQUIRED);
            var addingRes = repository.addExecutor(realm, restrictionExecutor);
            if (addingRes instanceof OperationResponse.Failure<String>) {
                return addingRes.map(it -> true);
            }
            var executorId = ((OperationResponse.Success<String>) addingRes).getData();

            var config = new AuthenticatorConfigRepresentation();
            config.setAlias(Realm.RESTRICTED_ROLE);
            config.setConfig(Map.of(
                    ACCESS_PROVIDER_ID, CLIENT_ROLE_PROVIDER,
                    MESSAGE, MESSAGE_VALUE
            ));
            return repository.addExecutorConfig(realm, executorId, config);
        } else {
            return flowRes.map(it -> true);
        }
    }

    public OperationResponse<AuthenticationFlowRepresentation> get(Realm realm, String alias) {
        log.debug("Getting flow {} in realm {} ...", alias, realm.name());
        return repository.find(realm, alias);
    }
}

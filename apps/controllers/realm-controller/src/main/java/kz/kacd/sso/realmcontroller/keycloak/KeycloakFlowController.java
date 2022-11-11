package kz.kacd.sso.realmcontroller.keycloak;

import kz.kacd.sso.realmcontroller.keycloak.model.Realm;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakFlowController {
    private static final String AUTHENTICATOR = "restrict-client-auth-authenticator";
    private static final Integer AUTHENTICATOR_PRIORITY = 31;
    private static final String REQUIRED = "REQUIRED";

    private final KeycloakFlowRepository repository;

    public OperationResponse<Boolean> createRestrictedFlow(Realm realm) {
        log.debug("Creating restricted flow for realm {} ...", realm.getName());
        var flowRes = repository.find(realm, Realm.RESTRICTED_AUTH_FLOW);
        if (flowRes instanceof OperationResponse.Failure<?> f) {
            if (f.getKind() == OperationResponse.K8sFailures.NOT_FOUND) {
                flowRes = repository.createCopy(realm, Realm.BROWSER_AUTH_FLOW, Realm.RESTRICTED_AUTH_FLOW);
            } else {
                return flowRes.map(it -> true);
            }
        }
        if (flowRes instanceof OperationResponse.Success<AuthenticationFlowRepresentation> s) {
            var flow = s.getData();
            var restrictionExecutor = new AuthenticationExecutionRepresentation();
            restrictionExecutor.setParentFlow(flow.getId());
            restrictionExecutor.setAuthenticatorFlow(false);
            restrictionExecutor.setAuthenticator(AUTHENTICATOR);
            restrictionExecutor.setPriority(AUTHENTICATOR_PRIORITY);
            restrictionExecutor.setRequirement(REQUIRED);
            return repository.addExecutor(realm, restrictionExecutor);
        } else {
            return flowRes.map(it -> true);
        }
    }
}

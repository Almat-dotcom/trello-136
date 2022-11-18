package kz.kacd.sso.realmcontroller.keycloak.flow;

import kz.kacd.sso.realmcontroller.keycloak.KeycloakClientFactory;
import kz.kacd.sso.realmcontroller.keycloak.Realm;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakFlowRepository {
    public static final String REQUIRED = "REQUIRED";

    private final KeycloakClientFactory factory;

    public OperationResponse<AuthenticationFlowRepresentation> createCopy(
            Realm realm,
            String fromAlias,
            String toAlias
    ) {
        log.debug("Creating new flow {} as a copy from {} in realm {} ...", toAlias, fromAlias, realm.name());
        try (var client = factory.create()) {
            var existing = find(realm, fromAlias);
            if (existing instanceof OperationResponse.Failure) {
                return existing;
            }
            var res = client.realm(realm.name()).flows().copy(fromAlias, Map.of("newName", toAlias));
            if ((res.getStatus() / 100) == 2) {
                return find(realm, toAlias);
            } else {
                return OperationResponse.internalError("Failed state of the response: " + res.getStatus() + "!");
            }
        } catch (Exception e) {
            log.error("Error on copying flows!", e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<AuthenticationFlowRepresentation> find(Realm realm, String alias) {
        log.debug("Finding authentication flow in realm {} by alias {} ...", realm.name(), alias);
        try (var client = factory.create()) {
            var res = client.realm(realm.name()).flows()
                    .getFlows()
                    .stream()
                    .filter(it -> it.getAlias() != null && it.getAlias().equals(alias))
                    .findFirst();
            if (res.isEmpty()) {
                return OperationResponse.notFound("Auth flow " + alias + " not found in realm " + realm.name());
            }
            return OperationResponse.success(res.get());
        } catch (Exception e) {
            log.error("Error on retrieving flow {} in realm {}!", alias, realm.name(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<String> addExecutor(
            Realm realm,
            AuthenticationExecutionRepresentation execution
    ) {
        log.debug("Adding execution {} on realm {} ...", execution.getAuthenticator(), realm.name());
        try (var client = factory.create()) {
            var res = client.realm(realm.name()).flows().addExecution(execution);
            if (res.getStatus() / 100 == 2) {
                var location = res.getHeaderString(HttpHeaders.LOCATION).split("/");
                return OperationResponse.success(location[location.length - 1]);
            } else {
                return OperationResponse.internalError("Unsuccessful response " + res.getStatus() + "!");
            }
        } catch (Exception e) {
            log.error("Error on updating flow!", e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<Boolean> addExecutorConfig(
            Realm realm,
            String authenticatorId,
            AuthenticatorConfigRepresentation config
    ) {
        try (var client = factory.create()) {
            client.realm(realm.name()).flows().newExecutionConfig(authenticatorId, config);
            return OperationResponse.success(true);
        } catch (Exception e) {
            log.error("Error on adding executor config!", e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<Boolean> markFormsAsRequired(
            Realm realm,
            AuthenticationFlowRepresentation flow
    ) {
        log.debug("Marking forms flow as required execution for flow {} in realm {} ...", flow.getAlias(), realm.name());
        try (var client = factory.create()) {
            var formsRes = findForms(client, realm, flow);
            if (formsRes.isEmpty()) {
                return OperationResponse.internalError("Cannot find form res inside " + flow.getAlias() + " flow!");
            }
            var forms = formsRes.get();
            forms.setRequirement(REQUIRED);
            client.realm(realm.name()).flows().updateExecutions(flow.getAlias(), forms);
            return OperationResponse.success(true);
        } catch (Exception e) {
            log.error("Error on marking forms for flow {} in realm {} as required!", flow.getAlias(), realm.name(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private Optional<AuthenticationExecutionInfoRepresentation> findForms(
            Keycloak keycloak,
            Realm realm,
            AuthenticationFlowRepresentation flow
    ) {
        log.debug("Finding forms flow for flow {} in realm {} ...", flow.getAlias(), realm.name());
        return keycloak.realm(realm.name()).flows().getExecutions(flow.getAlias())
                .stream()
                .filter(it -> it.getDisplayName() != null && it.getDisplayName().endsWith("forms"))
                .findFirst();
    }
}

package kz.kacd.sso.realmcontroller.keycloak.flow;

import kz.kacd.sso.realmcontroller.keycloak.KeycloakClientFactory;
import kz.kacd.sso.realmcontroller.keycloak.Realm;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakFlowRepository {

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

    public OperationResponse<Boolean> addExecutor(
            Realm realm,
            AuthenticationExecutionRepresentation execution
    ) {
        log.debug("Adding execution {} on realm {} ...", execution.getAuthenticator(), realm.name());
        try (var client = factory.create()) {
            var res = client.realm(realm.name()).flows().addExecution(execution);
            if (res.getStatus() / 100 == 2) {
                return OperationResponse.success(true);
            } else {
                return OperationResponse.internalError("Unsuccessful response " + res.getStatus() + "!");
            }
        } catch (Exception e) {
            log.error("Error on updating flow!", e);
            return OperationResponse.internalError(e.getMessage());
        }
    }
}

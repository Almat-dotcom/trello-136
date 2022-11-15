package kz.kacd.sso.realmcontroller.keycloak.group;

import kz.kacd.sso.realmcontroller.keycloak.KeycloakClientFactory;
import kz.kacd.sso.realmcontroller.keycloak.client.model.Client;
import kz.kacd.sso.realmcontroller.keycloak.group.model.Group;
import kz.kacd.sso.realmcontroller.keycloak.Realm;
import kz.kacd.sso.realmcontroller.keycloak.client.model.Role;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakGroupRepository {

    private final KeycloakClientFactory factory;

    public OperationResponse<Group> find(Realm realm, String name) {
        log.debug("Finding group {} in realm {} ...", name, realm.name());
        try (var keycloak = factory.create()) {
            var result = keycloak.realm(realm.name()).groups().groups()
                    .stream()
                    .filter(it -> it.getName().equals(name))
                    .findFirst();
            return result.map(representation -> OperationResponse.success(new Group(representation)))
                    .orElseGet(() ->
                            OperationResponse.notFound("Group " + name + " not found in realm " + realm.name() + "!")
                    );
        } catch (Exception e) {
            log.error("Error on finding group {} in realm {}!", name, realm.name(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<Boolean> addRole(Realm realm, Group group, Client client, Role role) {
        log.debug("Adding role {} to the group {} in realm {} ...", role.name(), group.name(), realm.name());
        try (var keycloak = factory.create()) {
            keycloak.realm(realm.name()).groups().group(group.id())
                    .roles()
                    .clientLevel(client.id())
                    .add(List.of(role.representation()));
            return OperationResponse.success(true);
        } catch (Exception e) {
            log.error("Error on adding role to group {} in realm {}!", group.name(), realm.name());
            return OperationResponse.internalError(e.getMessage());
        }
    }
}

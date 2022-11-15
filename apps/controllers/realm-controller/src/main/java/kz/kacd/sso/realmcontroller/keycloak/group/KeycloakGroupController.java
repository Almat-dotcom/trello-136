package kz.kacd.sso.realmcontroller.keycloak.group;

import kz.kacd.sso.realmcontroller.keycloak.Realm;
import kz.kacd.sso.realmcontroller.keycloak.client.model.Client;
import kz.kacd.sso.realmcontroller.keycloak.group.model.Group;
import kz.kacd.sso.realmcontroller.keycloak.client.model.Role;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakGroupController {

    private final KeycloakGroupRepository repository;

    public OperationResponse<Group> get(Realm realm, String group) {
        return repository.find(realm, group);
    }

    public OperationResponse<Boolean> addRole(Realm realm, Group group, Client client, Role role) {
        return repository.addRole(realm, group, client, role);
    }
}

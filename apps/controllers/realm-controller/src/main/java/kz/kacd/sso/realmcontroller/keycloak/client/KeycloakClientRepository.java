package kz.kacd.sso.realmcontroller.keycloak.client;

import kz.kacd.sso.realmcontroller.keycloak.KeycloakClientFactory;
import kz.kacd.sso.realmcontroller.keycloak.client.model.Client;
import kz.kacd.sso.realmcontroller.keycloak.Realm;
import kz.kacd.sso.realmcontroller.keycloak.client.model.Role;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakClientRepository {

    private final KeycloakClientFactory factory;

    public OperationResponse<Client> save(String realmName, Client client) {
        log.debug("Saving client {} ...", client.name());
        if (find(realmName, client.name()) instanceof OperationResponse.Success<Client> s) {
            client.setId(s.getData().id());
            return update(realmName, client);
        } else {
            return create(realmName, client);
        }
    }

    private OperationResponse<Client> update(String realmName, Client client) {
        log.debug("Updating client {} in realm {} ...", client.name(), realmName);
        try (var keycloak = factory.create()) {
            keycloak.realm(realmName).clients().get(client.id()).update(client.representation());
            return find(realmName, client.name());
        } catch (Exception e) {
            log.error("Error on updating client {} in realm {}!", client.name(), realmName, e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private OperationResponse<Client> create(String realmName, Client client) {
        log.debug("Creating new client {} in realm {} ...", client.name(), realmName);
        try (var keycloak = factory.create()) {
            var res = keycloak.realm(realmName).clients().create(client.representation());
            if (res.getStatus() / 100 == 2) {
                return find(realmName, client.name());
            } else {
                return OperationResponse.internalError("Unsuccessful status: " + res.getStatusInfo());
            }
        } catch (Exception e) {
            log.error("Error on creating new client {} in realm {}!", client.name(), realmName);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<Client> find(String realmName, String clientId) {
        log.debug("Finding client {} in realm {} ...", clientId, realmName);
        try (var keycloak = factory.create()) {
            var clients = keycloak.realm(realmName).clients().findAll();
            var found = clients.stream()
                    .filter(it -> it.getClientId().equals(clientId))
                    .findFirst();
            return found.map(representation ->
                    OperationResponse.success(new Client(representation))
            ).orElseGet(() ->
                    OperationResponse.notFound("Client " + clientId + " in realm " + realmName + " not found!")
            );
        } catch (Exception e) {
            log.error("Error on finding client {} in realm {}!", clientId, realmName, e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<Boolean> delete(String realmName, String clientId) {
        log.debug("Deleting client {} in realm {}...", clientId, realmName);
        var existing = find(realmName, clientId);
        if (existing instanceof OperationResponse.Failure<Client>) {
            return existing.map(it -> true);
        }

        var id = ((OperationResponse.Success<Client>) existing).getData().id();

        try (var keycloak = factory.create()) {
            keycloak.realm(realmName).clients().get(id).remove();
            return OperationResponse.success(true);
        } catch (Exception e) {
            log.error("Error on deleting client {} in realm {}!", clientId, realmName, e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<Boolean> addMappers(Realm realm, Client client, List<ProtocolMapperRepresentation> mappers) {
        log.debug("Adding mappers to client {} in realm {} ...", client.name(), realm.name());
        try (var keycloak = factory.create()) {
            var existing = findMappers(keycloak, realm, client);
            var existingNames = names(existing);
            var toCreate = mappers.stream()
                    .filter(it -> !existingNames.contains(it.getName()))
                    .toList();
            var newNames = names(mappers);
            var toDelete = existing.stream()
                    .filter(it -> !newNames.contains(it.getName()))
                    .toList();
            createMappers(keycloak, realm, client, toCreate);
            deleteMappers(keycloak, realm, client, toDelete);
            return OperationResponse.success(true);
        } catch (Exception e) {
            log.error("Error on adding mappers to client {} in realm {}!", client.name(), realm.name(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private List<ProtocolMapperRepresentation> findMappers(
            Keycloak keycloak,
            Realm realm,
            Client client
    ) {
        log.debug("Finding mappers for client {} in realm {} ...", client.name(), realm.name());
        return keycloak.realm(realm.name()).clients().get(client.id()).getProtocolMappers().getMappers();
    }

    private List<String> names(List<ProtocolMapperRepresentation> mappers) {
        return mappers.stream().map(ProtocolMapperRepresentation::getName).toList();
    }

    private void createMappers(
            Keycloak keycloak,
            Realm realm,
            Client client,
            List<ProtocolMapperRepresentation> mappers
    ) {
        if (mappers.isEmpty()) {
            return;
        }
        log.debug("Creating mappers for client {} in realm {} ...", client.name(), realm.name());
        keycloak.realm(realm.name()).clients().get(client.id()).getProtocolMappers().createMapper(mappers);
    }

    private void deleteMappers(
            Keycloak keycloak,
            Realm realm,
            Client client,
            List<ProtocolMapperRepresentation> mappers
    ) {
        if (mappers.isEmpty()) {
            return;
        }
        log.debug("Deleting mappers for client {} in realm {} ...", client.name(), realm.name());
        var resource =
                keycloak.realm(realm.name()).clients().get(client.id()).getProtocolMappers();
        mappers.forEach(it -> resource.delete(it.getId()));
    }

    public OperationResponse<Role> getRole(Realm realm, Client client, String roleName) {
        log.debug("Finding role {} for client {} in realm {} ...", roleName, client.name(), realm.name());
        try (var keycloak = factory.create()) {
            var result = keycloak.realm(realm.name())
                    .clients().get(client.id())
                    .roles().get(roleName).toRepresentation();
            return OperationResponse.success(new Role(result));
        } catch (Exception e) {
            log.error("Error on finding role {} for client {} in realm {}!", roleName, client.name(), realm.name());
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<Boolean> addRoles(Realm realm, Client client, List<Role> roles) {
        log.debug("Adding roles to client {} in realm {} ...", client.name(), realm.name());
        try (var keycloak = factory.create()) {
            var existing = getAllRoles(keycloak, realm, client);
            var existingNames = roleNames(existing);
            var toCreate = roles.stream().filter(it -> !existingNames.contains(it.name())).toList();
            var newNames = roleNames(roles);
            var toDelete = existing.stream().filter(it -> !newNames.contains(it.name())).toList();
            createRoles(keycloak, realm, client, toCreate);
            deleteRoles(keycloak, realm, client, toDelete);
            return OperationResponse.success(true);
        } catch (Exception e) {
            log.error("Error on adding roles for client {} in realm {}!", client.name(), realm.name());
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private List<Role> getAllRoles(Keycloak keycloak, Realm realm, Client client) {
        log.debug("Getting all roles for client {} in realm {} ...", client.name(), realm.name());
        return keycloak.realm(realm.name()).clients().get(client.id()).roles().list().stream().map(Role::new).toList();
    }

    private List<String> roleNames(List<Role> roles) {
        return roles.stream().map(Role::name).toList();
    }

    private void createRoles(Keycloak keycloak, Realm realm, Client client, List<Role> roles) {
        if (roles.isEmpty()) {
            return;
        }
        log.debug("Creating roles for client {} in realm {} ...", client.name(), realm.name());
        var resource = keycloak.realm(realm.name()).clients().get(client.id()).roles();
        roles.stream().map(Role::representation).forEach(resource::create);
    }

    private void deleteRoles(Keycloak keycloak, Realm realm, Client client, List<Role> roles) {
        if (roles.isEmpty()) {
            return;
        }
        log.debug("Deleting roles for client {} in realm {} ...", client.name(), realm.name());
        var resource = keycloak.realm(realm.name()).clients().get(client.id()).roles();
        roles.forEach(it -> resource.deleteRole(it.name()));
    }
}

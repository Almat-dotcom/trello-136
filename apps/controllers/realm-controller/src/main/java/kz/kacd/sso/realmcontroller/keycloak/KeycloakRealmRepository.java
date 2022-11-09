package kz.kacd.sso.realmcontroller.keycloak;

import kz.kacd.sso.realmcontroller.keycloak.model.Realm;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakRealmRepository {

    private final KeycloakClientFactory factory;

    public OperationResponse<Realm> save(Realm realm) {
        log.debug("Saving realm {} ...", realm.getName());
        if (find(realm.getName()) instanceof OperationResponse.Success<Realm>) {
            return update(realm);
        } else {
            return create(realm);
        }
    }

    public OperationResponse<Realm> find(String name) {
        log.debug("Finding realm with name {} ...", name);
        try (var client = factory.create()) {
            var all = client.realms().findAll();
            var result = all.stream().filter(it -> it.getRealm().equals(name)).findFirst();
            if (result.isEmpty()) {
                return OperationResponse.notFound("Realm with id " + name + " not found!");
            }
            return OperationResponse.success(new Realm(result.get()));
        } catch (Exception e) {
            log.error("Error on finding realm!", e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private OperationResponse<Realm> create(Realm realm) {
        log.debug("Creating new realm {} ...", realm.getName());
        try (var client = factory.create()) {
            client.realms().create(realm.representation());
            return OperationResponse.success(realm);
        } catch (Exception e) {
            log.error("Error on creating new realm!", e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private OperationResponse<Realm> update(Realm realm) {
        log.debug("Updating existing realm {} ...", realm.getName());
        try (var client = factory.create()) {
            client.realm(realm.getName()).update(realm.representation());
            return OperationResponse.success(realm);
        } catch (Exception e) {
            log.error("Error on updating realm {} ...", realm.getName());
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<Boolean> delete(Realm realm) {
        log.debug("Deleting realm {} ...", realm.getName());
        try (var client = factory.create()) {
            client.realm(realm.getName()).remove();
            return OperationResponse.success(true);
        } catch (Exception e) {
            log.error("Error on deleting realm {} ...", realm.getName());
            return OperationResponse.internalError(e.getMessage());
        }
    }
}

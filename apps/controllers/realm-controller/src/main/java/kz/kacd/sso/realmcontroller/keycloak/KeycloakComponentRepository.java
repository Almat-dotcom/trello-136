package kz.kacd.sso.realmcontroller.keycloak;

import kz.kacd.sso.realmcontroller.keycloak.model.Realm;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakComponentRepository {

    private final KeycloakClientFactory factory;

    public OperationResponse<ComponentRepresentation> save(Realm realm, ComponentRepresentation component) {
        log.debug("Saving component ...");
        if (find(realm, component) instanceof OperationResponse.Success<ComponentRepresentation> s) {
            component.setId(s.getData().getId());
            component.setParentId(s.getData().getParentId());
            return update(realm, component);
        } else {
            return create(realm, component);
        }
    }

    private OperationResponse<ComponentRepresentation> create(Realm realm, ComponentRepresentation component) {
        log.debug("Creating component ...");
        try (var client = factory.create()) {
            var res = client.realm(realm.getName()).components().add(component);
            if ((res.getStatus() / 100) == 2) {
                return find(realm, component);
            } else {
                return OperationResponse.internalError("Failed creating component!");
            }
        } catch (Exception e) {
            log.error("Error on creating component {} {} ...", realm.getName(), component.getName(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private OperationResponse<ComponentRepresentation> update(Realm realm, ComponentRepresentation component) {
        log.debug("Updating component ...");
        try (var client = factory.create()) {
            client.realm(realm.getName()).components().component(component.getId()).update(component);
            return find(realm, component);
        } catch (Exception e) {
            log.error("Error on updating component {} {} ...", realm.getName(), component.getName(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private OperationResponse<ComponentRepresentation> find(Realm realm, ComponentRepresentation component) {
        log.debug("Finding component in keycloak ...");
        try (var client = factory.create()) {
            var result = client
                    .realm(realm.getName())
                    .components()
                    .query()
                    .stream()
                    .filter(
                            it -> it.getName() != null
                                    && it.getName().equals(component.getName())
                                    && it.getParentId().equals(component.getParentId())
                    )
                    .findFirst();
            return result
                    .map(OperationResponse::success)
                    .orElseGet(() ->
                            OperationResponse.notFound(
                                    "Component with name "
                                            + component.getName()
                                            + " in realm "
                                            + realm.getName()
                                            + " not found!"
                            )
                    );
        } catch (Exception e) {
            log.error("Error on finding component {} ...", realm.getName(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }
}

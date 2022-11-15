package kz.kacd.sso.realmcontroller.keycloak.component;

import kz.kacd.sso.realmcontroller.keycloak.ExtendedKeycloak;
import kz.kacd.sso.realmcontroller.keycloak.KeycloakClientFactory;
import kz.kacd.sso.realmcontroller.keycloak.Realm;
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
    private final ExtendedKeycloak ext;

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
            var res = client.realm(realm.name()).components().add(component);
            if ((res.getStatus() / 100) == 2) {
                return find(realm, component);
            } else {
                return OperationResponse.internalError("Failed creating component!");
            }
        } catch (Exception e) {
            log.error("Error on creating component {} {} ...", realm.name(), component.getName(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private OperationResponse<ComponentRepresentation> update(Realm realm, ComponentRepresentation component) {
        log.debug("Updating component ...");
        try (var client = factory.create()) {
            client.realm(realm.name()).components().component(component.getId()).update(component);
            return find(realm, component);
        } catch (Exception e) {
            log.error("Error on updating component {} {} ...", realm.name(), component.getName(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    private OperationResponse<ComponentRepresentation> find(Realm realm, ComponentRepresentation component) {
        log.debug("Finding component in keycloak ...");
        try (var client = factory.create()) {
            var result = client
                    .realm(realm.name())
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
                                            + realm.name()
                                            + " not found!"
                            )
                    );
        } catch (Exception e) {
            log.error("Error on finding component {} ...", realm.name(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<ComponentRepresentation> find(Realm realm, String providerId) {
        return find(realm, realm.id(), providerId);
    }

    public OperationResponse<ComponentRepresentation> find(Realm realm, String parentId, String providerId) {
        log.debug("Finding component with provider {} in realm {} ...", providerId, realm.name());
        try (var client = factory.create()) {
            var result = client.realm(realm.name())
                    .components()
                    .query()
                    .stream()
                    .filter(it ->
                            it.getProviderId() != null
                                    && it.getProviderId().equals(providerId)
                                    && it.getParentId().equals(parentId)
                    )
                    .findFirst();
            return result.map(OperationResponse::success)
                    .orElseGet(() ->
                            OperationResponse.notFound("Provider " + providerId + " in realm " + realm.name() + " not found!")
                    );
        } catch (Exception e) {
            log.error("Error on finding provider {} in realm {}!", providerId, realm.name(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }

    public OperationResponse<Boolean> sync(Realm realm, ComponentRepresentation component, boolean reversed) {
        log.debug("Syncing component {} in realm {} ...", component.getName(), realm.name());
        try {
            ext.sync(realm.name(), component, reversed);
            return OperationResponse.success(true);
        } catch (Exception e) {
            log.error("Error on syncing component {} in realm {}!", component.getName(), realm.name(), e);
            return OperationResponse.internalError(e.getMessage());
        }
    }
}

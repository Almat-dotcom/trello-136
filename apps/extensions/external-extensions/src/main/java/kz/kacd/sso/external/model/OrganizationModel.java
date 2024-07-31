package kz.kacd.sso.external.model;

import kz.kacd.sso.external.representation.PageRepresentation;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface OrganizationModel {

    String getId();

    String getBin();

    void setBin(String bin);

    String getName();

    void setName(String name);

    String getDisplayName();

    void setDisplayName(String displayName);

    boolean enabled();

    void enable();

    void disable();

    RealmModel getRealm();

    UserModel getCreatedBy();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    PositionModel getHead();

    Stream<PositionModel> getPositions();

    PageRepresentation<PositionModel> getPositions(String position, String userId, int from, int limit);

    PositionModel getPosition(UserModel user);

    default PositionModel getPosition(String id) {
        return getPositions().filter(it -> it.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Creates position for user in this organization without confirmation.
     */
    PositionModel requestPosition(String positionName, UserModel user);

    /**
     * Marks position as confirmed.
     */
    void confirmPosition(PositionModel position);

    /**
     * Marks position as revoked.
     */
    void revokePosition(PositionModel position);

    /**
     * Removes membership if user has been removed.
     */
    void removePosition(PositionModel position);

    /**
     * Searches clients.
     *
     * @param active show only active or non-active
     */
    List<ClientModel> getClients(Boolean active);

    /**
     * Gets organization's client by clientId
     */
    ClientModel getClient(String clientId);

    /**
     * Creates new client for organization.
     *
     * @param clientId    id of the client
     * @param description description
     * @param scopes      list of scopes available for client
     */
    String createClient(String clientId, String description, List<String> scopes);

    /**
     * Updates client
     */
    void updateClient(String clientId, String description, boolean active);

    /**
     * Creates new client secret
     */
    String resetClientSecret(String clientId);

    interface OrganizationEvent extends ProviderEvent {
        OrganizationModel getOrganization();

        KeycloakSession getKeycloakSession();

        RealmModel getRealm();
    }

    interface OrganizationCreatedEvent extends OrganizationEvent {
    }

    interface OrganizationRemovedEvent extends OrganizationEvent {
    }
}

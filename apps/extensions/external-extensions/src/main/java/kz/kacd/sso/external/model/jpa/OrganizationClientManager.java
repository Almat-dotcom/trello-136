package kz.kacd.sso.external.model.jpa;

import kz.kacd.sso.external.model.jpa.entity.OrganizationEntity;
import kz.kacd.sso.external.model.jpa.exception.ClientAlreadyExistsException;
import kz.kacd.sso.external.model.jpa.exception.ClientNotFoundException;
import kz.kacd.sso.external.model.jpa.exception.InvalidListOfScopesException;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import org.keycloak.authentication.authenticators.client.ClientIdAndSecretAuthenticator;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.*;
import org.keycloak.services.managers.ClientManager;
import org.keycloak.services.managers.RealmManager;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrganizationClientManager {

    private final KeycloakSession session;
    private final OrganizationEntity entity;
    private final RealmModel realm;

    public OrganizationClientManager(KeycloakSession session, OrganizationEntity entity, RealmModel realm) {
        this.session = session;
        this.entity = entity;
        this.realm = realm;
    }

    public List<ClientModel> getClients(Boolean active) {
        return session.users().searchForUserByUserAttributeStream(realm, ExternalRegistrationPage.FIELD_BIN, entity.getBin())
                .filter(user -> user.getServiceAccountClientLink() != null)
                .map(user -> session.clients().getClientById(realm, user.getServiceAccountClientLink()))
                .filter(Objects::nonNull)
                .filter(client -> active == null || active.equals(client.isEnabled()))
                .collect(Collectors.toList());
    }

    public String createClient(String clientId, String description, List<String> scopes) {
        if (session.clients().getClientByClientId(realm, clientId) != null) {
            throw new ClientAlreadyExistsException(clientId);
        }
        List<ClientScopeModel> scopeModels = session.clientScopes().getClientScopesStream(realm)
                .filter(it -> scopes.contains(it.getName()))
                .collect(Collectors.toList());
        if (scopes.size() != scopeModels.size()) {
            throw new InvalidListOfScopesException(scopes);
        }

        // Creating client and setting base attributes
        ClientModel client = realm.addClient(clientId);
        client.setEnabled(true);
        client.setProtocol("openid-connect");
        client.setName(clientId);
        client.setDescription(description);
        // Making client confidential with service account capabilities
        client.setRedirectUris(new HashSet<>());
        client.setWebOrigins(new HashSet<>());
        client.setPublicClient(false);
        client.setBearerOnly(false);
        client.setServiceAccountsEnabled(true);
        // Settings client secret authentication
        client.setClientAuthenticatorType(ClientIdAndSecretAuthenticator.PROVIDER_ID);
        client.setSecret(SecretGenerator.getInstance().randomString());

        // Creating service account for this client
        ClientManager manager = new ClientManager(new RealmManager(session));
        manager.enableServiceAccount(client);

        // Adding all requested scopes
        scopeModels.forEach(scope -> client.addClientScope(scope, false));

        // Adding bin to service account
        UserModel sa = session.users().getServiceAccount(client);
        sa.setSingleAttribute(ExternalRegistrationPage.FIELD_BIN, entity.getBin());

        return client.getSecret();
    }

    public void updateClient(String clientId, String description, boolean active) {
        ClientModel client = getClient(clientId);

        // Updating client's attributes
        client.setDescription(description);
        client.setEnabled(active);
    }

    public String resetClientSecret(String clientId) {
        ClientModel client = getClient(clientId);
        client.setSecret(SecretGenerator.getInstance().randomString());
        return client.getSecret();
    }

    public ClientModel getClient(String clientId) {
        ClientModel client = session.clients().getClientByClientId(realm, clientId);
        if (client == null) {
            throw new ClientNotFoundException(entity.getBin(), clientId);
        }
        UserModel sa = session.users().getServiceAccount(client);
        if (sa == null) {
            throw new ClientNotFoundException(entity.getBin(), clientId);
        }
        String bin = sa.getFirstAttribute(ExternalRegistrationPage.FIELD_BIN);
        if (!Objects.equals(bin, entity.getBin())) {
            throw new ClientNotFoundException(entity.getBin(), clientId);
        }

        return client;
    }
}

package kz.kacd.sso.client.config;

import kz.kacd.sso.v1.ClientSpec;
import org.jboss.logging.Logger;
import org.keycloak.authentication.authenticators.client.ClientIdAndSecretAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.ClientModel;

import java.util.HashSet;

public class DefaultClientConfigurer implements ClientConfigurer {
    private static final Logger log = Logger.getLogger(DefaultClientConfigurer.class);

    private final KeycloakSession session;

    public DefaultClientConfigurer(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void configure(RealmModel realm, String clientId, ClientSpec client) {
        log.info("Configuring default client ALMAT " + clientId);
        if (client == null) {
            return;
        }

        log.info("Configuring default client ALMAT 2" + clientId);

        log.infof("Configuring client %s in realm %s ...", clientId, realm.getName());
        ClientModel target = realm.getClientByClientId(clientId);
        if (target == null) {
            target = create(realm, clientId);
        }

        target.setEnabled(true);
        target.setProtocol("openid-connect");
        setDisplayProps(target, clientId, client);
        setAccess(target, client.getAccess());
        setCapability(target, client.getCapability());
    }

    private ClientModel create(RealmModel realm, String clientId) {
        return realm.addClient(clientId);
    }

    private void setDisplayProps(ClientModel client, String clientId, ClientSpec spec) {
        if (spec.getDisplayedName() != null) {
            client.setName(spec.getDisplayedName());
        } else {
            client.setName(clientId);
        }
        client.setDescription(spec.getDisplayedDescription());
    }

    private void setAccess(ClientModel client, kz.kacd.sso.v1.clientspec.Access access) {
        if (access == null) {
            return;
        }

        client.setRootUrl(access.getRootUrl());
        client.setBaseUrl(access.getHomeUrl());
        if (access.getValidRedirectUris() != null) {
            client.setRedirectUris(new HashSet<>(access.getValidRedirectUris()));
        }
        if (access.getWebOrigins() != null) {
            client.setWebOrigins(new HashSet<>(access.getWebOrigins()));
        }
    }

    private void setCapability(ClientModel client, kz.kacd.sso.v1.clientspec.Capability capability) {
        if (capability == null) {
            // По умолчанию confidential
            client.setPublicClient(false);
            client.setBearerOnly(false);
            client.setServiceAccountsEnabled(true);
            client.setClientAuthenticatorType(ClientIdAndSecretAuthenticator.PROVIDER_ID);
            client.setSecret("default-secret-" + client.getClientId());
            return;
        }

        switch (capability.getType()) {
            case PUBLIC:
                client.setPublicClient(true);
                client.setBearerOnly(false);
                client.setSecret(null);
                break;
            case CONFIDENTIAL:
                client.setPublicClient(false);
                client.setBearerOnly(false);
                client.setServiceAccountsEnabled(true);
                client.setClientAuthenticatorType(ClientIdAndSecretAuthenticator.PROVIDER_ID);
                if (capability.getClientSecretPlain() != null) {
                    client.setSecret(capability.getClientSecretPlain());
                } else {
                    client.setSecret("confidential-secret-" + client.getClientId());
                }
                break;
            case BEARER_ONLY:
                client.setPublicClient(false);
                client.setBearerOnly(true);
                client.setClientAuthenticatorType(ClientIdAndSecretAuthenticator.PROVIDER_ID);
                if (capability.getClientSecretPlain() != null) {
                    client.setSecret(capability.getClientSecretPlain());
                } else {
                    client.setSecret("bearer-secret-" + client.getClientId());
                }
                break;
            default:
                // По умолчанию confidential
                client.setPublicClient(false);
                client.setBearerOnly(false);
                client.setServiceAccountsEnabled(true);
                client.setClientAuthenticatorType(ClientIdAndSecretAuthenticator.PROVIDER_ID);
                client.setSecret("default-secret-" + client.getClientId());
        }
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

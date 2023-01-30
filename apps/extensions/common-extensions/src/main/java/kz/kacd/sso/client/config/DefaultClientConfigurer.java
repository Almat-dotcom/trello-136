package kz.kacd.sso.client.config;

import kz.kacd.sso.client.config.model.ProtocolMapperFactory;
import kz.kacd.sso.federation.FederationConfigurer;
import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.secret.Secret;
import kz.kacd.sso.k8s.secret.SecretValueProvider;
import kz.kacd.sso.realm.flow.AuthFlowConstants;
import kz.kacd.sso.v1.ClientSpec;
import kz.kacd.sso.v1.clientspec.*;
import org.jboss.logging.Logger;
import org.keycloak.authentication.authenticators.client.ClientIdAndSecretAuthenticator;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.services.managers.ClientManager;
import org.keycloak.services.managers.RealmManager;

import java.util.*;

import static kz.kacd.sso.util.ValueUtils.defaulted;

public class DefaultClientConfigurer implements ClientConfigurer {
    private static final Logger log = Logger.getLogger(DefaultClientConfigurer.class);

    private static final String POST_LOGOUT_URIS = "post.logout.redirect.uris";

    private final KeycloakSession session;

    public DefaultClientConfigurer(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void configure(RealmModel realm, String clientId, ClientSpec client) {
        if (client == null) {
            return;
        }

        log.debugf("Configuring client %s in realm %s ...", clientId, realm.getName());
        ClientModel target = realm.getClientByClientId(clientId);
        if (target == null) {
            target = create(realm, clientId);
        }

        target.setEnabled(true);
        target.setProtocol("openid-connect");
        setDisplayProps(target, clientId, client);
        setAccess(target, client.getAccess());
        setCapability(target, client.getCapability());
        addScope(realm, client.getScope());
        addRoles(target, client.getRoles());
        addLdap(realm, target, client.getLdap());
        addMappers(target, client.getAttributes());
        setAllowedGroup(realm, target, client.getAllowedGroup());
    }

    private ClientModel create(RealmModel realm, String clientId) {
        return realm.addClient(clientId);
    }

    private void setDisplayProps(ClientModel client, String clientId, ClientSpec spec) {
        client.setName(defaulted(spec.getDisplayedName(), clientId));
        client.setDescription(spec.getDisplayedDescription());
    }

    private void setAccess(ClientModel client, Access access) {
        Access spec = access;
        if (spec == null) {
            spec = new Access();
        }

        client.setRootUrl(spec.getRootUrl());
        client.setBaseUrl(spec.getHomeUrl());
        Set<String> uris = new HashSet<>(defaulted(spec.getValidRedirectUris(), Collections.emptyList()));
        client.setRedirectUris(uris);
        client.setAttribute(POST_LOGOUT_URIS, String.join("##", uris));
        client.setWebOrigins(new HashSet<>(defaulted(spec.getWebOrigins(), Collections.emptySet())));
    }

    private void setCapability(ClientModel client, Capability capability) {
        Capability spec = capability;
        if (spec == null) {
            spec = new Capability();
        }

        switch (defaulted(spec.getType(), Capability.Type.PUBLIC)) {
            case PUBLIC:
                configurePublicClient(client);
                break;
            case CONFIDENTIAL:
                configureConfidentialClient(client, spec);
                break;
            case BEARER_ONLY:
                configureBearerOnlyClient(client, spec);
                break;
        }
    }

    private void configurePublicClient(ClientModel client) {
        client.setPublicClient(true);
        client.setBearerOnly(false);
        client.setSecret(null);
    }

    private void configureConfidentialClient(ClientModel client, Capability spec) {
        client.setPublicClient(false);
        client.setBearerOnly(false);
        client.setServiceAccountsEnabled(true);
        client.setClientAuthenticatorType(ClientIdAndSecretAuthenticator.PROVIDER_ID);
        setSecret(client, spec);
        enableServiceAccount(client);
    }

    private void configureBearerOnlyClient(ClientModel client, Capability spec) {
        client.setPublicClient(false);
        client.setBearerOnly(true);
        client.setClientAuthenticatorType(ClientIdAndSecretAuthenticator.PROVIDER_ID);
        setSecret(client, spec);
    }

    private void setSecret(ClientModel client, Capability spec) {
        Secret secret = null;
        if (K8sConfig.ENABLED && spec.getClientExistingSecret() != null) {
            secret = session.getProvider(SecretValueProvider.class).findByName(spec.getClientExistingSecret());
        }

        if (spec.getClientSecretPlain() != null) {
            client.setSecret(spec.getClientSecretPlain());
        } else if (secret != null) {
            client.setSecret(secret.get(spec.getClientSecretKey()));
        } else {
            throw new IllegalStateException("Cannot find existing secret for client!");
        }
    }

    private void enableServiceAccount(ClientModel client) {
        ClientManager manager = new ClientManager(new RealmManager(session));
        manager.enableServiceAccount(client);
    }

    private void addScope(RealmModel realm, Scope scope) {
        if (scope == null || scope.getName() == null) {
            return;
        }

        ClientScopeModel model = realm.getClientScopesStream()
                .filter(it -> it.getName().equals(scope.getName()))
                .findAny()
                .orElseGet(() -> realm.addClientScope(scope.getName()));

        model.setDescription(scope.getScopeDescription());
    }

    private void addRoles(ClientModel client, List<Roles> roles) {
        if (roles == null) {
            return;
        }
        roles.forEach(it -> {
            RoleModel role = client.addRole(it.getName());
            role.setDescription(it.getRoleDescription());
        });
    }

    private void addLdap(RealmModel realm, ClientModel client, Ldap spec) {
        if (spec == null || spec.getDn() == null) {
            return;
        }

        FederationConfigurer federations = session.getProvider(FederationConfigurer.class);
        ComponentModel ldap = federations.findLdap(realm);
        if (ldap == null) {
            return;
        }

        federations.addRoleMapping(realm, ldap, client.getClientId(), spec.getDn());
    }

    private void addMappers(ClientModel client, List<ClientSpec.Attributes> attrs) {
        if (attrs == null || attrs.isEmpty()) {
            return;
        }

        attrs.forEach(kind -> {
            ProtocolMapperModel model = ProtocolMapperFactory.create(kind);
            if (model == null) {
                log.error("Cannot create module with type " + kind + "!");
            }
            ProtocolMapperModel existing = client.getProtocolMapperByName(client.getProtocol(), model.getName());
            if (existing == null) {
                client.addProtocolMapper(model);
            } else {
                client.updateProtocolMapper(model);
            }
        });
    }

    private void setAllowedGroup(RealmModel realm, ClientModel client, String group) {
        if (group == null || group.isEmpty()) {
            return;
        }

        Optional<GroupModel> allowedGroup = realm.getGroupsStream()
                .filter(it -> it.getName().equals(group))
                .findFirst();
        if (!allowedGroup.isPresent()) {
            return;
        }

        RoleModel restricted = client.addRole("restricted-access");
        restricted.setDescription("Role to restrict access to client");
        allowedGroup.get().grantRole(restricted);
        AuthenticationFlowModel flow = realm.getFlowByAlias(AuthFlowConstants.RESTRICTED_BROWSER);
        client.setAuthenticationFlowBindingOverride("browser", flow.getId());
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

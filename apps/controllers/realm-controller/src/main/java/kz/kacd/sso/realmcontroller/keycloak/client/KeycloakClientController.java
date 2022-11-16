package kz.kacd.sso.realmcontroller.keycloak.client;

import kz.kacd.sso.realmcontroller.k8s.model.K8sAction;
import kz.kacd.sso.realmcontroller.k8s.model.K8sClient;
import kz.kacd.sso.realmcontroller.keycloak.Realm;
import kz.kacd.sso.realmcontroller.keycloak.client.model.*;
import kz.kacd.sso.realmcontroller.keycloak.component.KeycloakFederationController;
import kz.kacd.sso.realmcontroller.keycloak.flow.KeycloakFlowController;
import kz.kacd.sso.realmcontroller.keycloak.group.KeycloakGroupController;
import kz.kacd.sso.realmcontroller.keycloak.group.model.Group;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakClientController {

    private final KeycloakClientRepository repository;
    private final KeycloakFederationController federations;
    private final KeycloakFlowController flows;
    private final KeycloakGroupController groups;

    /**
     * Applies client configuration on realm.
     * </>
     * To apply client config properly it requires lots of communications
     * with different resources in realm:
     * - client
     * - protocol mapper
     * - component
     * - role
     * - authentication flow
     * - group
     */
    public OperationResponse<Client> apply(Realm realm, K8sClient source) {
        if (source.action() == K8sAction.DELETED) {
            federations.deleteLdapRoleMapper(realm, source.name());
            return repository.delete(realm.name(), source.name()).map(it -> null);
        }

        var existing = repository.find(realm.name(), source.name());
        ClientBuilder builder;
        if (existing instanceof OperationResponse.Success<Client> s) {
            builder = s.getData().update();
        } else {
            builder = Client.newInstance();
        }
        var clientRes = repository.save(realm.name(), builder.buildFrom(source));
        if (clientRes instanceof OperationResponse.Failure) {
            return clientRes;
        }
        var client = ((OperationResponse.Success<Client>) clientRes).getData();

        var ldapRes = addLdapMapper(realm, client, source);
        if (ldapRes instanceof OperationResponse.Failure) {
            return ldapRes.map(it -> client);
        }
        var ldap = ((OperationResponse.Success<ComponentRepresentation>) ldapRes).getData();

        var mappersRes = addMappers(realm, client, source);
        if (mappersRes instanceof OperationResponse.Failure) {
            return mappersRes;
        }

        var rolesRes = addRoles(realm, client, ldap, source);
        if (rolesRes instanceof OperationResponse.Failure) {
            return rolesRes.map(it -> client);
        }
        var restrictedRole = ((OperationResponse.Success<Role>) rolesRes).getData();

        return restrictGroupAccess(realm, client, restrictedRole, source);
    }

    /**
     * Creates client roles mappers to LDAP provider.
     */
    private OperationResponse<ComponentRepresentation> addLdapMapper(Realm realm, Client client, K8sClient source) {
        if (source.spec() == null || source.spec().getLdap() == null || source.spec().getLdap().getDn() == null) {
            return OperationResponse.success(null);
        }

        var ldap = federations.getLdap(realm);
        if (ldap instanceof OperationResponse.Failure) {
            return OperationResponse.internalError(
                    "LDAP provider in realm " + realm.name() + " not found! Can't configure client!"
            );
        }

        return federations.addLdapRoleMapper(
                realm,
                ((OperationResponse.Success<ComponentRepresentation>) ldap).getData().getId(),
                client.name(),
                source.spec().getLdap().getDn()
        );
    }

    /**
     * Adds protocol mapper to client.
     */
    private OperationResponse<Client> addMappers(Realm realm, Client client, K8sClient source) {
        if (source.spec() == null || source.spec().getAttributes() == null || source.spec().getAttributes().isEmpty()) {
            return OperationResponse.success(client);
        }

        var builder = new ClientMappersBuilder();
        source.spec().getAttributes()
                .forEach(it -> {
                    switch (it) {
                        case MIDDLE_NAME -> builder.withMiddleName();
                        case GROUPS -> builder.withGroups();
                        case LOCALE -> builder.withLocale();
                    }
                });

        return repository.addMappers(realm, client, builder.build()).map(it -> client);
    }

    /**
     * Adds roles to client.
     *
     * @return restricted access role or null.
     */
    private OperationResponse<Role> addRoles(Realm realm, Client client, ComponentRepresentation ldap, K8sClient source) {
        if (source.spec() == null || source.spec().getRoles() == null || source.spec().getRoles().isEmpty()) {
            return OperationResponse.success(null);
        }

        var roles = new ArrayList<>(
                source.spec().getRoles()
                        .stream()
                        .map(it ->
                                new RoleBuilder(it.getName())
                                        .forClient(client)
                                        .withDescription(it.getDescription())
                                        .build()
                        )
                        .toList()
        );
        if (source.spec().getAllowedGroup() != null) {
            roles.add(RoleBuilder.restricted().forClient(client).withDescription("Restrict access to client").build());
        }
        var result = repository.addRoles(realm, client, roles);
        if (result instanceof OperationResponse.Failure) {
            return result.map(it -> null);
        }

        if (ldap != null) {
            federations.sync(realm, ldap, false);
        }

        if (source.spec().getAllowedGroup() != null) {
            return repository.getRole(realm, client, RoleBuilder.RESTRICTED);
        } else {
            return OperationResponse.success(null);
        }
    }

    /**
     * Adds restricted role to specified group and changes browser auth flow to restricted.
     */
    private OperationResponse<Client> restrictGroupAccess(Realm realm, Client client, Role restricted, K8sClient source) {
        if (source.spec() == null || source.spec().getAllowedGroup() == null || restricted == null) {
            return OperationResponse.success(client);
        }

        var syncRes = federations.importGroups(realm);
        if (syncRes instanceof OperationResponse.Failure) {
            return syncRes.map(it -> client);
        }

        var groupRes = groups.get(realm, source.spec().getAllowedGroup());
        if (groupRes instanceof OperationResponse.Failure) {
            return groupRes.map(it -> client);
        }
        var group = ((OperationResponse.Success<Group>) groupRes).getData();

        var addRoleRes = groups.addRole(realm, group, client, restricted);
        if (addRoleRes instanceof OperationResponse.Failure) {
            return addRoleRes.map(it -> client);
        }

        var restrictFlow = flows.get(realm, Realm.RESTRICTED_AUTH_FLOW);
        if (restrictFlow instanceof OperationResponse.Failure) {
            return restrictFlow.map(it -> client);
        }
        var flowId = ((OperationResponse.Success<AuthenticationFlowRepresentation>) restrictFlow).getData()
                .getId();

        client.representation().setAuthenticationFlowBindingOverrides(Map.of("browser", flowId));
        return repository.save(realm.name(), client);
    }
}

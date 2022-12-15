package kz.kacd.sso.federation;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.mappers.LDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.membership.group.GroupLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.membership.role.RoleLDAPStorageMapper;
import org.keycloak.storage.user.SynchronizationResult;

public class DefaultFederationSynchronizer implements FederationSynchronizer {

    private final KeycloakSession session;

    public DefaultFederationSynchronizer(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void syncToKeycloak(RealmModel realm, ComponentModel ldap, ComponentModel component) {
        session.getProvider(UserStorageProvider.class, ldap);
        LDAPStorageMapper mapper = session.getProvider(LDAPStorageMapper.class, component);

        SynchronizationResult result = mapper.syncDataFromFederationProviderToKeycloak(realm);
        if (result.getFailed() != 0) {
            throw new IllegalStateException("Failed to sync mapping to keycloak");
        }
    }

    @Override
    public void syncToLdap(RealmModel realm, ComponentModel ldap, ComponentModel component) {
        session.getProvider(UserStorageProvider.class, ldap);
        LDAPStorageMapper mapper = session.getProvider(LDAPStorageMapper.class, component);

        SynchronizationResult result = mapper.syncDataFromKeycloakToFederationProvider(realm);
        if (result.getFailed() != 0) {
            throw new IllegalStateException("Failed to sync mapping to keycloak");
        }
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

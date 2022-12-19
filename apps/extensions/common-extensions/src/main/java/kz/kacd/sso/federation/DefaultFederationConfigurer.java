package kz.kacd.sso.federation;

import kz.kacd.sso.federation.model.*;
import kz.kacd.sso.v1.FederationSpec;
import kz.kacd.sso.v1.federationspec.ldap.Searching;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderEvent;

public class DefaultFederationConfigurer implements FederationConfigurer {
    private static final Logger log = Logger.getLogger(DefaultFederationConfigurer.class);

    private final KeycloakSession session;

    public DefaultFederationConfigurer(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void configure(RealmModel realm, FederationSpec spec) {
        log.debugf("Configuring federation for realm %s ...", realm.getName());
        configureLdap(realm, spec);
    }

    @Override
    public ComponentModel findLdap(RealmModel realm) {
        return realm.getComponentsStream()
                .filter(it -> it.getProviderId().equals(LdapBuilder.LDAP_PROVIDER_ID))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addRoleMapping(RealmModel realm, ComponentModel parent, String client, String dn) {
        ComponentModel mapper =
                new LdapRoleMapperBuilder(parent.getId())
                        .withClientAndDn(client, dn)
                        .build();
        realm.addComponentModel(mapper);
        session.getTransactionManager().commit();
        session.getTransactionManager().begin();
        session.getKeycloakSessionFactory().publish(groupsMapperCreated(realm, parent, mapper, true));
    }

    private void configureLdap(RealmModel realm, FederationSpec spec) {
        if (spec == null || spec.getLdap() == null) {
            return;
        }

        ComponentModel source = new LdapBuilder(realm.getId(), session).buildFrom(spec.getLdap());
        ComponentModel ldap = realm.getComponentsStream().filter(it ->
                it.getName() != null && it.getName().equals(spec.getLdap().getDisplayedName())
        ).findFirst().orElse(null);
        if (ldap == null) {
            ldap = realm.addComponentModel(source);
        } else {
            source.setId(ldap.getId());
            realm.updateComponent(source);
            ldap = source;
        }
        addMappers(realm, ldap, spec);
    }

    private void addMappers(RealmModel realm, ComponentModel ldap, FederationSpec spec) {
        boolean readOnly = true;
        if (spec != null && spec.getLdap() != null && spec.getLdap().getSearching() != null) {
            readOnly = spec.getLdap().getSearching().getMode() == Searching.Mode.R;
        }

        addMiddleName(realm, ldap, readOnly);
        addFirstName(realm, ldap, readOnly);
        addLastName(realm, ldap, readOnly);
        addDivision(realm, ldap);
        addGroups(realm, ldap, spec);
        if (!readOnly) {
            addRealmManagementRolesMapper(realm, ldap, spec.getLdap().getAdvanced().getRealmManagementRolesDn());
        }
    }

    private void addMiddleName(RealmModel realm, ComponentModel parent, boolean readOnly) {
        realm.addComponentModel(
                new LdapAttributeMapperBuilder(parent)
                        .withName("Middle Name")
                        .withAttributeMapping(
                                "middleName",
                                "middleName",
                                readOnly,
                                false
                        )
                        .build()
        );
    }

    private void addFirstName(RealmModel realm, ComponentModel parent, boolean readOnly) {
        realm.addComponentModel(
                new LdapAttributeMapperBuilder(parent)
                        .withName("First Name")
                        .withAttributeMapping(
                                "givenName",
                                "firstName",
                                readOnly,
                                false
                        )
                        .build()
        );
    }

    private void addLastName(RealmModel realm, ComponentModel parent, boolean readOnly) {
        realm.addComponentModel(
                new LdapAttributeMapperBuilder(parent)
                        .withName("Last Name")
                        .withAttributeMapping(
                                "sn",
                                "lastName",
                                readOnly,
                                false
                        )
                        .build()
        );
    }

    private void addDivision(RealmModel realm, ComponentModel parent) {
        realm.addComponentModel(
                new LdapDivisionMapperBuilder(parent)
                        .withName("Division")
                        .withAttributeMapping("distinguishedName", "division")
                        .build()
        );
    }

    private void addGroups(RealmModel realm, ComponentModel parent, FederationSpec spec) {
        if (spec == null || spec.getLdap() == null || spec.getLdap().getGroups() == null) {
            return;
        }

        new LdapGroupMapperBuilder(parent)
                .withSpec(spec.getLdap().getGroups())
                .build()
                .forEach(it -> {
                    realm.addComponentModel(it);
                    session.getTransactionManager().commit();
                    session.getTransactionManager().begin();
                    session.getKeycloakSessionFactory().publish(groupsMapperCreated(realm, parent, it, false));
                });
    }

    private void addRealmManagementRolesMapper(RealmModel realm, ComponentModel parent, String dn) {
        if (dn == null || dn.isEmpty()) {
            return;
        }

        KeycloakModelUtils.runJobInTransaction(
                session.getKeycloakSessionFactory(),
                keycloakSession -> {
                    RealmModel model = keycloakSession.realms().getRealm(realm.getId());
                    ComponentModel component =
                            new LdapRoleMapperBuilder(parent.getId()).withClientAndDn("realm-management", dn).build();
                    model.addComponentModel(component);
                    keycloakSession.getTransactionManager().commit();
                    keycloakSession.getTransactionManager().begin();
                    keycloakSession.getKeycloakSessionFactory().publish(groupsMapperCreated(model, parent, component, true));
                }
        );
    }

    private ProviderEvent groupsMapperCreated(RealmModel realm, ComponentModel ldap, ComponentModel component, boolean syncToLdap) {
        return new GroupsOrRolesMapperConfigured() {
            @Override
            public KeycloakSession getSession() {
                return session;
            }

            @Override
            public ComponentModel getMapper() {
                return component;
            }

            @Override
            public boolean syncToLdap() {
                return syncToLdap;
            }

            @Override
            public RealmModel getRealm() {
                return realm;
            }

            @Override
            public ComponentModel getLdap() {
                return ldap;
            }
        };
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

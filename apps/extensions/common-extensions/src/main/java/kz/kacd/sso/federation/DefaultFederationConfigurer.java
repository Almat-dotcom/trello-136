package kz.kacd.sso.federation;

import kz.kacd.sso.federation.model.LdapAttributeMapperBuilder;
import kz.kacd.sso.federation.model.LdapBuilder;
import kz.kacd.sso.federation.model.LdapDivisionMapperBuilder;
import kz.kacd.sso.federation.model.LdapGroupMapperBuilder;
import kz.kacd.sso.v1.FederationSpec;
import kz.kacd.sso.v1.federationspec.ldap.Searching;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

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

    private void configureLdap(RealmModel realm, FederationSpec spec) {
        if (spec == null) {
            return;
        }

        ComponentModel ldap = realm.addComponentModel(
                new LdapBuilder(realm.getId(), session).buildFrom(spec.getLdap())
        );
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
                .forEach(realm::addComponentModel);
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

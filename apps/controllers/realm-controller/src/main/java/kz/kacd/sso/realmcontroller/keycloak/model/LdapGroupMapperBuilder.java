package kz.kacd.sso.realmcontroller.keycloak.model;

import kz.kacd.sso.realmcontroller.k8s.crd.model.federation.ldap.LdapGroupsMappingSpec;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.representations.idm.ComponentRepresentation;

import java.util.List;

import static kz.kacd.sso.realmcontroller.util.ValueUtils.defaulted;

public class LdapGroupMapperBuilder {
    private static final String PROVIDER_ID = "group-ldap-mapper";
    private static final String PROVIDER_TYPE = "org.keycloak.storage.ldap.mappers.LDAPStorageMapper";
    private static final String DROP_NON_EXISTING = "drop.non.existing.groups.during.sync";
    private static final String GROUP_NAME_ATTR = "group.name.ldap.attribute";
    private static final String GROUP_CLASS = "group.object.classes";
    private static final String GROUP_CLASS_DEFAULT = "group";
    private static final String DN = "groups.dn";
    private static final String FILTER = "groups.ldap.filter";
    private static final String PATH = "groups.path";
    private static final String IGNORE_MISSING = "ignore.missing.groups";
    private static final String MEMBER_OF_ATTR = "memberof.ldap.attribute";
    private static final String MEMBER_OF_ATTR_DEFAULT = "memberOf";
    private static final String ATTR_TYPE = "membership.attribute.type";
    private static final String ATTR_TYPE_DEFAULT = "DN";
    private static final String ATTR = "membership.ldap.attribute";
    private static final String MEMBERSHIP_LDAP_ATTR = "membership.user.ldap.attribute";
    private static final String MEMBERSHIP_LDAP_ATTR_DEFAULT = "cn";
    private static final String MODE = "mode";
    private static final String HIERARCHY = "preserve.group.inheritance";
    private static final String STRATEGY = "user.roles.retrieve.strategy";
    private static final String DEFAULT_STRATEGY = "LOAD_GROUPS_BY_MEMBER_ATTRIBUTE";

    private final ComponentRepresentation target = new ComponentRepresentation();

    public LdapGroupMapperBuilder(ComponentRepresentation parent) {
        target.setParentId(parent.getId());
        target.setName("LDAP groups");
        target.setProviderId(PROVIDER_ID);
        target.setProviderType(PROVIDER_TYPE);
    }

    public LdapGroupMapperBuilder withSpec(LdapGroupsMappingSpec spec) {
        var config = new MultivaluedHashMap<String, String>();
        config.put(DROP_NON_EXISTING, List.of(defaulted(spec.getDropNonExisting(), false).toString()));
        config.put(GROUP_NAME_ATTR, List.of(defaulted(spec.getName(), "cn")));
        config.put(GROUP_CLASS, List.of(GROUP_CLASS_DEFAULT));
        config.put(DN, List.of(spec.getDn()));
        if (spec.getFilter() != null) {
            config.put(FILTER, List.of(spec.getFilter()));
        }
        config.put(PATH, List.of(defaulted(spec.getPath(), "/")));
        config.put(IGNORE_MISSING, List.of(defaulted(spec.getIgnoreMissing(), false).toString()));
        config.put(ATTR_TYPE, List.of(ATTR_TYPE_DEFAULT));
        config.put(ATTR, List.of(defaulted(spec.getAttributeName(), "member")));
        config.put(MODE, List.of(defaulted(spec.getMode(), LdapGroupsMappingSpec.Modes.R).getCode()));
        config.put(HIERARCHY, List.of(defaulted(spec.getHierarchy(), true).toString()));
        config.put(STRATEGY, List.of(DEFAULT_STRATEGY));
        config.put(MEMBER_OF_ATTR, List.of(MEMBER_OF_ATTR_DEFAULT));
        config.put(MEMBERSHIP_LDAP_ATTR, List.of(MEMBERSHIP_LDAP_ATTR_DEFAULT));
        target.setConfig(config);
        return this;
    }

    public ComponentRepresentation build() {
        return target;
    }
}

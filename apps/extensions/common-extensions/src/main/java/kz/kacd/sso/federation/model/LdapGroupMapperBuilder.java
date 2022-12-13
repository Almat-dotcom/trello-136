package kz.kacd.sso.federation.model;

import kz.kacd.sso.v1.federationspec.ldap.Groups;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.representations.idm.ComponentRepresentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static kz.kacd.sso.util.ValueUtils.defaulted;

public class LdapGroupMapperBuilder {
    public static final String PROVIDER_ID = "group-ldap-mapper";
    private static final String PROVIDER_TYPE = "org.keycloak.storage.ldap.mappers.LDAPStorageMapper";
    private static final String DROP_NON_EXISTING = "drop.non.existing.groups.during.sync";
    private static final String GROUP_NAME_ATTR = "group.name.ldap.attribute";
    private static final String GROUP_CLASS = "group.object.classes";
    private static final String GROUP_CLASS_DEFAULT = "group";
    private static final String DN = "groups.dn";
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

    private final List<ComponentModel> target = new ArrayList<>();
    private final ComponentModel parent;

    public LdapGroupMapperBuilder(ComponentModel parent) {
        this.parent = parent;
    }

    public LdapGroupMapperBuilder withSpec(Groups spec) {
        if (spec == null || spec.getMappings() == null || spec.getMappings().isEmpty()) {
            return this;
        }

        target.addAll(
                spec.getMappings().stream()
                        .map(it -> withSpec(spec, it.getDn(), it.getPath()))
                        .collect(Collectors.toList())
        );

        return this;
    }

    private ComponentModel withSpec(Groups spec, String dn, String path) {
        ComponentModel result = init();

        MultivaluedHashMap<String, String> config = new MultivaluedHashMap<>();
        config.put(
                DROP_NON_EXISTING,
                Collections.singletonList(defaulted(spec.getDropNonExisting(), false).toString())
        );
        config.put(GROUP_NAME_ATTR, Collections.singletonList(defaulted(spec.getName(), "cn")));
        config.put(GROUP_CLASS, Collections.singletonList(GROUP_CLASS_DEFAULT));
        config.put(DN, Collections.singletonList(dn));
        config.put(PATH, Collections.singletonList(defaulted(path, "/")));
        config.put(IGNORE_MISSING, Collections.singletonList(defaulted(spec.getIgnoreMissing(), false).toString()));
        config.put(ATTR_TYPE, Collections.singletonList(ATTR_TYPE_DEFAULT));
        config.put(ATTR, Collections.singletonList(defaulted(spec.getAttributeName(), "member")));
        config.put(
                MODE,
                Collections.singletonList(
                        modes(defaulted(spec.getMode(), Groups.Mode.R))
                )
        );
        config.put(HIERARCHY, Collections.singletonList(defaulted(spec.getHierarchy(), true).toString()));
        config.put(STRATEGY, Collections.singletonList(DEFAULT_STRATEGY));
        config.put(MEMBER_OF_ATTR, Collections.singletonList(MEMBER_OF_ATTR_DEFAULT));
        config.put(MEMBERSHIP_LDAP_ATTR, Collections.singletonList(MEMBERSHIP_LDAP_ATTR_DEFAULT));
        result.setConfig(config);

        return result;
    }

    private String modes(Groups.Mode mode) {
        switch (mode) {
            case R:
                return "READ_ONLY";
            case RW:
                return "LDAP_ONLY";
            case RWC:
                return "IMPORT";
            default:
                return null;
        }
    }

    private ComponentModel init() {
        ComponentModel result = new ComponentModel();
        result.setParentId(parent.getId());
        result.setName("LDAP groups");
        result.setProviderId(PROVIDER_ID);
        result.setProviderType(PROVIDER_TYPE);
        return result;
    }

    public List<ComponentModel> build() {
        return target;
    }
}

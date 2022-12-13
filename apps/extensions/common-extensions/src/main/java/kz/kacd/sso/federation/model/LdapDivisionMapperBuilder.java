package kz.kacd.sso.federation.model;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;

import java.util.Collections;

public class LdapDivisionMapperBuilder {
    public static final String USER_MODEL_ATTRIBUTE = "user.model.attribute";
    public static final String DN_ATTRIBUTE = "ldap.dn.attribute";
    private static final String PROVIDER_ID = "ldap-user-division-mapper";
    private static final String PROVIDER_TYPE = "org.keycloak.storage.ldap.mappers.LDAPStorageMapper";
    private final ComponentModel target = new ComponentModel();

    public LdapDivisionMapperBuilder(ComponentModel parent) {
        target.setParentId(parent.getId());
        target.setProviderType(PROVIDER_TYPE);
        target.setProviderId(PROVIDER_ID);
    }

    public LdapDivisionMapperBuilder withName(String name) {
        target.setName(name);
        return this;
    }

    public LdapDivisionMapperBuilder withAttributeMapping(String from, String to) {
        MultivaluedHashMap<String, String> config = new MultivaluedHashMap<>();
        config.put(USER_MODEL_ATTRIBUTE, Collections.singletonList(to));
        config.put(DN_ATTRIBUTE, Collections.singletonList(from));
        target.setConfig(config);
        return this;
    }

    public ComponentModel build() {
        return target;
    }
}

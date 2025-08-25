package kz.kacd.sso.federation.model;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;

import java.util.Collections;

public class LdapAttributeMapperBuilder {
    private static final String PROVIDER_ID = "user-attribute-ldap-mapper";
    private static final String PROVIDER_TYPE = "org.keycloak.storage.ldap.mappers.LDAPStorageMapper";
    private static final String ALWAYS_READ_VALUE = "always.read.value.from.ldap";
    private static final String REQUIRED_IN_LDAP = "is.mandatory.in.ldap";
    private static final String LDAP_ATTR = "ldap.attribute";
    private static final String READ_ONLY = "read.only";
    private static final String USER_MODEL_ATTR = "user.model.attribute";

    private final ComponentModel target = new ComponentModel();

    public LdapAttributeMapperBuilder(ComponentModel parent) {
        target.setParentId(parent.getId());
        target.setProviderType(PROVIDER_TYPE);
        target.setProviderId(PROVIDER_ID);
    }

    public LdapAttributeMapperBuilder withName(String name) {
        target.setName(name);
        return this;
    }

    public LdapAttributeMapperBuilder withAttributeMapping(
            String from,
            String to,
            boolean readOnly,
            boolean required
    ) {
        MultivaluedHashMap<String, String> config = new MultivaluedHashMap<>();
        config.put(ALWAYS_READ_VALUE, Collections.singletonList("true"));
        config.put(REQUIRED_IN_LDAP, Collections.singletonList(Boolean.toString(required)));
        config.put(LDAP_ATTR, Collections.singletonList(from));
        config.put(READ_ONLY, Collections.singletonList(Boolean.toString(readOnly)));
        config.put(USER_MODEL_ATTR, Collections.singletonList(to));
        target.setConfig(config);
        return this;
    }

    public ComponentModel build() {
        return target;
    }
}

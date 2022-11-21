package kz.kacd.sso.realmcontroller.keycloak.component.model;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.representations.idm.ComponentRepresentation;

import java.util.List;

public class LdapDivisionMapperBuilder {
    private static final String PROVIDER_ID = "ldap-user-division-mapper";
    private static final String PROVIDER_TYPE = "org.keycloak.storage.ldap.mappers.LDAPStorageMapper";
    public static final String USER_MODEL_ATTRIBUTE = "user.model.attribute";
    public static final String DN_ATTRIBUTE = "ldap.dn.attribute";

    private final ComponentRepresentation target = new ComponentRepresentation();

    public LdapDivisionMapperBuilder(ComponentRepresentation parent) {
        target.setParentId(parent.getId());
        target.setProviderType(PROVIDER_TYPE);
        target.setProviderId(PROVIDER_ID);
    }

    public LdapDivisionMapperBuilder withName(String name) {
        target.setName(name);
        return this;
    }

    public LdapDivisionMapperBuilder withAttributeMapping(String from, String to) {
        var config = new MultivaluedHashMap<String, String>();
        config.put(USER_MODEL_ATTRIBUTE, List.of(to));
        config.put(DN_ATTRIBUTE, List.of(from));
        target.setConfig(config);
        return this;
    }

    public ComponentRepresentation build() {
        return target;
    }
}

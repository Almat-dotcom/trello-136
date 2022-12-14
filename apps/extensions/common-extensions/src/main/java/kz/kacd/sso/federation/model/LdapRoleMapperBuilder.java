package kz.kacd.sso.federation.model;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.representations.idm.ComponentRepresentation;

import java.util.Collections;
import java.util.List;

public class LdapRoleMapperBuilder {

    public static final String PROVIDER_ID = "role-ldap-mapper";
    private static final String PROVIDER_TYPE = "org.keycloak.storage.ldap.mappers.LDAPStorageMapper";
    private static final String ROLE_NAME_IN_USER_ATTR = "membership.user.ldap.attribute";
    private static final String ROLE_NAME_ATTR = "role.name.ldap.attribute";
    private static final String MEMBERSHIP_ATTR = "membership.ldap.attribute";
    private static final String CLIENT_ID = "client.id";
    private static final String REALM_ROLE_MAPPING = "use.realm.roles.mapping";
    private static final String ROLE_GROUP_CLASSES = "role.object.classes";
    private static final String DN = "roles.dn";
    private static final String MEMBER_OF_ATTR = "memberof.ldap.attribute";
    private static final String MEMBERSHIP_ATTR_TYPE = "membership.attribute.type";
    private static final String MODE = "mode";
    private static final String LDAP_ONLY = "LDAP_ONLY";
    private static final String STRATEGY = "user.roles.retrieve.strategy";
    private static final String DEFAULT_STRATEGY = "LOAD_ROLES_BY_MEMBER_ATTRIBUTE";

    private final ComponentModel target = new ComponentModel();

    public LdapRoleMapperBuilder(String parentId) {
        target.setParentId(parentId);
        target.setProviderId(PROVIDER_ID);
        target.setProviderType(PROVIDER_TYPE);
    }

    public LdapRoleMapperBuilder withClientAndDn(String clientId, String dn) {
        target.setName(clientId + "-roles-mapper");
        target.setConfig(mapperConfig(clientId, dn));

        return this;
    }

    private MultivaluedHashMap<String, String> mapperConfig(String clientId, String dn) {
        MultivaluedHashMap<String, String> result = new MultivaluedHashMap<>();
        result.put(ROLE_NAME_IN_USER_ATTR, Collections.singletonList("cn"));
        result.put(ROLE_NAME_ATTR, Collections.singletonList("cn"));
        result.put(MEMBERSHIP_ATTR, Collections.singletonList("member"));
        result.put(CLIENT_ID, Collections.singletonList(clientId));
        result.put(REALM_ROLE_MAPPING, Collections.singletonList("false"));
        result.put(ROLE_GROUP_CLASSES, Collections.singletonList("group"));
        result.put(DN, Collections.singletonList(dn));
        result.put(MEMBER_OF_ATTR, Collections.singletonList("memberOf"));
        result.put(MEMBERSHIP_ATTR_TYPE, Collections.singletonList("DN"));
        result.put(MODE, Collections.singletonList(LDAP_ONLY));
        result.put(STRATEGY, Collections.singletonList(DEFAULT_STRATEGY));
        return result;
    }

    public ComponentModel build() {
        return target;
    }
}

package kz.kacd.sso.ldap.mapper;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.UserModelDelegate;
import org.keycloak.models.utils.reflection.Property;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.LDAPUtils;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.Condition;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;

import java.util.*;
import java.util.stream.Stream;

public class UserDivisionLdapMapper extends AbstractLDAPStorageMapper {
    private static final Logger log = Logger.getLogger(UserDivisionLdapMapper.class);

    private static final Map<String, Property<Object>> userModelProperties = LDAPUtils.getUserModelProperties();

    public static final String USER_MODEL_ATTRIBUTE = "user.model.attribute";
    public static final String DN_ATTRIBUTE = "ldap.dn.attribute";

    public UserDivisionLdapMapper(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
    }

    @Override
    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm, boolean create) {
        String userModelProperty = getUserModelAttribute();
        Property<Object> userProperty = userModelProperties.get(userModelProperty.toLowerCase());

        String ldapValue = getUserDivision(ldapUser);

        if (userProperty != null) {
            userProperty.setValue(user, ldapUser);
        } else {
            if (ldapValue != null) {
                user.setSingleAttribute(userModelProperty, ldapValue);
            } else {
                user.removeAttribute(userModelProperty);
            }
        }
    }

    @Override
    public void onRegisterUserToLDAP(LDAPObject ldapObject, UserModel userModel, RealmModel realmModel) {
        log.debug("Nothing to add when user is registering.");
    }

    @Override
    public UserModel proxy(LDAPObject ldapUser, UserModel delegate, RealmModel realm) {
        String userModelAttrName = getUserModelAttribute();
        String ldapAttrName = getDnAttribute();

        delegate = new UserModelDelegate(delegate) {

            @Override
            public String getFirstAttribute(String name) {
                if (name.equalsIgnoreCase(userModelAttrName)) {
                    return getUserDivision(ldapUser);
                } else {
                    return super.getFirstAttribute(name);
                }
            }

            @Override
            public Stream<String> getAttributeStream(String name) {
                if (name.equalsIgnoreCase(userModelAttrName)) {
                    String value = getUserDivision(ldapUser);
                    if (value == null) {
                        return Stream.empty();
                    } else {
                        return Stream.of(value);
                    }
                } else {
                    return super.getAttributeStream(name);
                }
            }

            @Override
            public Map<String, List<String>> getAttributes() {
                Map<String, List<String>> attrs = new HashMap<>(super.getAttributes());

                // Ignore UserModel properties
                if (userModelProperties.get(userModelAttrName.toLowerCase()) != null) {
                    return attrs;
                }

                String ldapAttr = getUserDivision(ldapUser);
                if (ldapAttr != null) {
                    attrs.put(userModelAttrName, Collections.singletonList(ldapAttr));
                }
                return attrs;
            }

            @Override
            public String getEmail() {
                if (UserModel.EMAIL.equalsIgnoreCase(userModelAttrName)) {
                    return getUserDivision(ldapUser);
                } else {
                    return super.getEmail();
                }
            }

            @Override
            public boolean isEnabled() {
                if (UserModel.ENABLED.equalsIgnoreCase(userModelAttrName)) {
                    return Boolean.parseBoolean(ldapUser.getAttributeAsString(ldapAttrName));
                } else {
                    return super.isEnabled();
                }
            }

            @Override
            public boolean isEmailVerified() {
                if (UserModel.EMAIL_VERIFIED.equalsIgnoreCase(userModelAttrName)) {
                    return Boolean.parseBoolean(ldapUser.getAttributeAsString(ldapAttrName));
                } else {
                    return super.isEmailVerified();
                }
            }

            @Override
            public String getLastName() {
                if (UserModel.LAST_NAME.equalsIgnoreCase(userModelAttrName)) {
                    return getUserDivision(ldapUser);
                } else {
                    return super.getLastName();
                }
            }

            @Override
            public String getFirstName() {
                if (UserModel.FIRST_NAME.equalsIgnoreCase(userModelAttrName)) {
                    return getUserDivision(ldapUser);
                } else {
                    return super.getFirstName();
                }
            }

        };
        return delegate;
    }

    @Override
    public void beforeLDAPQuery(LDAPQuery query) {
        String userModelAttrName = getUserModelAttribute();
        String ldapAttrName = getDnAttribute();

        // Add mapped attribute to returning ldap attributes
        query.addReturningLdapAttribute(ldapAttrName);
        query.addReturningReadOnlyLdapAttribute(ldapAttrName);

        // Change conditions and use ldapAttribute instead of userModel
        for (Condition condition : query.getConditions()) {
            condition.updateParameterName(userModelAttrName, ldapAttrName);
            String parameterName = condition.getParameterName();
            if (parameterName != null && (parameterName.equalsIgnoreCase(userModelAttrName) || parameterName.equalsIgnoreCase(ldapAttrName))) {
                condition.setBinary(false);
            }
        }
    }

    private String getUserDivision(LDAPObject ldapUser) {
        String ldapDnAttribute = getDnAttribute();
        String dnValue = ldapUser.getAttributeAsString(ldapDnAttribute);
        if (dnValue == null) {
            return null;
        }
        String[] dnParts = dnValue.split(",");
        if (dnParts.length < 2) {
            return null;
        }

        return dnParts[1].replace("OU=", "");
    }

    private String getUserModelAttribute() {
        return mapperModel.getConfig().getFirst(USER_MODEL_ATTRIBUTE);
    }

    private String getDnAttribute() {
        return mapperModel.getConfig().getFirst(DN_ATTRIBUTE);
    }
}

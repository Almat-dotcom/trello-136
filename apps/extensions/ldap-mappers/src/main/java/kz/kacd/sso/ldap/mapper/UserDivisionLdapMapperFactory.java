package kz.kacd.sso.ldap.mapper;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.ldap.LDAPConfig;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapperFactory;
import org.keycloak.storage.ldap.mappers.LDAPConfigDecorator;

import java.util.List;

public class UserDivisionLdapMapperFactory extends AbstractLDAPStorageMapperFactory implements LDAPConfigDecorator {

    public static final String PROVIDER_ID = "ldap-user-division-mapper";
    protected static final List<ProviderConfigProperty> config;

    static {
        config = getConfigProps();
    }

    @Override
    public String getHelpText() {
        return "Used to retrieve last organizational unit for user from LDAP' DN.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return config;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    protected AbstractLDAPStorageMapper createMapper(ComponentModel componentModel, LDAPStorageProvider ldapStorageProvider) {
        return new UserDivisionLdapMapper(componentModel, ldapStorageProvider);
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties(RealmModel realm, ComponentModel parent) {
        return getConfigProps();
    }

    static List<ProviderConfigProperty> getConfigProps() {
        ProviderConfigurationBuilder builder = ProviderConfigurationBuilder.create()
                .property().name(UserDivisionLdapMapper.USER_MODEL_ATTRIBUTE)
                .label("User Model Attribute")
                .helpText("Name of the UserModel property or attribute you want to map the LDAP attribute into.")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("division")
                .add()
                .property().name(UserDivisionLdapMapper.DN_ATTRIBUTE)
                .label("Distinguished Name Attribute")
                .helpText("Name of the LDAP attribute which contains distinguished name.")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("dn")
                .add();
        return builder.build();
    }

    @Override
    public void updateLDAPConfig(LDAPConfig ldapConfig, ComponentModel componentModel) {

    }
}

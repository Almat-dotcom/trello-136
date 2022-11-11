package kz.kacd.sso.realmcontroller.keycloak.model;

import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.ldap.*;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.representations.idm.ComponentRepresentation;

import java.util.List;
import java.util.Map;

import static kz.kacd.sso.realmcontroller.util.ValueUtils.defaulted;
import static kz.kacd.sso.realmcontroller.util.ValueUtils.defaultedPeriod;

public class LdapBuilder {
    private static final String LDAP_PROVIDER_ID = "ldap";
    private static final String LDAP_PROVIDER_TYPE = "org.keycloak.storage.UserStorageProvider";
    private static final String ALLOW_KERBEROS_AUTH = "allowKerberosAuthentication";
    private static final String AUTH_TYPE = "authType";
    private static final String BATCH_SIZE = "batchSizeForSync";
    private static final String BIND_CRED = "bindCredential";
    private static final String BIND_DN = "bindDn";
    private static final String CACHE_POLICY = "cachePolicy";
    private static final String CHANGED_SYNC_PERIOD = "changedSyncPeriod";
    private static final String CONNECTION_POOLING = "connectionPooling";
    private static final String CONNECTION_URL = "connectionUrl";
    private static final String EDIT_MODE = "editMode";
    private static final String ENABLED = "enabled";
    private static final String FULL_SYNC_PERIOD = "fullSyncPeriod";
    private static final String IMPORT_ENABLED = "importEnabled";
    private static final String PAGINATION = "pagination";
    private static final String RDN_LDAP_ATTR = "rdnLDAPAttribute";
    private static final String SEARCH_SCOPE = "searchScope";
    private static final String START_TLS = "startTls";
    private static final String SYNC_REGISTRATIONS = "syncRegistrations";
    private static final String TRUST_EMAIL = "trustEmail";
    private static final String KERBEROS_FOR_PASSWORD = "useKerberosForPasswordAuthentication";
    private static final String LDAP3 = "usePasswordModifyExtendedOp";
    private static final String USER_TRUSTSTORE_SPI = "useTruststoreSpi";
    private static final String USER_CLASSES = "userObjectClasses";
    private static final String DEFAULT_USER_CLASSES = "person, organizationalPerson, user";
    private static final String USERNAME_ATTR = "usernameLDAPAttribute";
    private static final String USERS_DN = "usersDn";
    private static final String UUID_ATTR = "uuidLDAPAttribute";
    private static final String VALIDATE_PASSWORD = "validatePasswordPolicy";
    private static final String VENDOR = "vendor";
    private static final String FILTER = "customUserSearchFilter";

    private final String realmId;
    private final ComponentRepresentation target = new ComponentRepresentation();

    public LdapBuilder(String realmId) {
        this.realmId = realmId;
    }

    public ComponentRepresentation buildFrom(
            LdapSpec spec,
            Map<String, SecretData> secrets
    ) {
        if (spec == null) {
            return build();
        }

        return withLdap(spec, secrets)
                .build();
    }

    private LdapBuilder withLdap(LdapSpec spec, Map<String, SecretData> secrets) {
        if (spec == null) {
            return this;
        }

        target.setParentId(realmId);
        target.setName(spec.getDisplayedName());
        target.setProviderId(LDAP_PROVIDER_ID);
        target.setProviderType(LDAP_PROVIDER_TYPE);
        target.setConfig(ldapConfig(spec, secrets));
        return this;
    }

    private MultivaluedHashMap<String, String> ldapConfig(LdapSpec spec, Map<String, SecretData> secrets) {
        var result = new MultivaluedHashMap<String, String>();
        result.put(ALLOW_KERBEROS_AUTH, List.of("false"));
        result.put(
                AUTH_TYPE,
                List.of(
                        defaulted(spec.getConnection().getAuth().getType(), LdapAuthSpec.LdapAuthBindings.SIMPLE)
                                .getCode()
                )
        );
        result.put(BATCH_SIZE, List.of(defaulted(spec.getSync().getBatch(), 1_000).toString()));
        bindCreds(result, spec, secrets);
        var cache = spec.getCache();
        if (spec.getCache() == null) {
            cache = new LdapCacheSpec();
        }
        result.put(CACHE_POLICY, List.of(defaulted(cache.getPolicy(), LdapCacheSpec.Policies.DEFAULT).name()));
        result.put(
                CHANGED_SYNC_PERIOD,
                List.of(
                        defaultedPeriod(
                                defaulted(spec.getSync().getChangedSync(), false),
                                spec.getSync().getChangedSyncPeriod(),
                                "1m"
                        )
                )
        );
        result.put(
                CONNECTION_POOLING,
                List.of(defaulted(spec.getConnection().getConnectionPooling(), false).toString())
        );
        result.put(CONNECTION_URL, List.of(spec.getConnection().getUrl()));
        result.put(EDIT_MODE, List.of(defaulted(spec.getSearching().getMode(), LdapSearchingSpec.Modes.R).getCode()));
        result.put(ENABLED, List.of("true"));
        result.put(
                FULL_SYNC_PERIOD,
                List.of(
                        defaultedPeriod(
                                defaulted(spec.getSync().getFullSync(), false),
                                spec.getSync().getFullSyncPeriod(),
                                "1m"
                        )
                )
        );
        result.put(IMPORT_ENABLED, List.of(defaulted(spec.getSync().getImportUsers(), true).toString()));
        result.put(PAGINATION, List.of(defaulted(spec.getSearching().getPagination(), false).toString()));
        result.put(RDN_LDAP_ATTR, List.of(defaulted(spec.getSearching().getUsername(), "cn")));
        result.put(SEARCH_SCOPE, List.of(defaulted(spec.getSearching().getScope(), LdapSearchingSpec.Scopes.FLAT).getCode()));
        result.put(START_TLS, List.of(defaulted(spec.getConnection().getStartTls(), false).toString()));
        result.put(SYNC_REGISTRATIONS, List.of(defaulted(spec.getSync().getSyncRegistrations(), false).toString()));
        result.put(TRUST_EMAIL, List.of(defaulted(spec.getAdvanced().getTrustEmail(), true).toString()));
        result.put(KERBEROS_FOR_PASSWORD, List.of("false"));
        result.put(LDAP3, List.of("false"));
        result.put(
                USER_TRUSTSTORE_SPI,
                List.of(defaulted(spec.getConnection().getUseTrustStore(), LdapConnectionSpec.UseTrustStoreModes.LDAPS_ONLY)
                        .getCode())
        );
        result.put(USER_CLASSES, List.of(defaulted(spec.getSearching().getClasses(), DEFAULT_USER_CLASSES)));
        result.put(USERNAME_ATTR, List.of(defaulted(spec.getSearching().getUsername(), "cn")));
        result.put(USERS_DN, List.of(spec.getSearching().getUsersDn()));
        result.put(UUID_ATTR, List.of(spec.getSearching().getUuid(), "cn"));
        result.put(VALIDATE_PASSWORD, List.of(defaulted(spec.getAdvanced().getValidatePassword(), false).toString()));
        result.put(VENDOR, List.of(defaulted(spec.getVendor(), LdapSpec.LdapVendors.ACTIVE_DIRECTORY).getCode()));
        if (spec.getSearching().getFilter() != null) {
            result.put(FILTER, List.of(spec.getSearching().getFilter()));
        }
        return result;
    }

    private void bindCreds(MultivaluedHashMap<String, String> result, LdapSpec spec, Map<String, SecretData> secrets) {
        if (!secrets.containsKey(spec.getConnection().getAuth().getExistingSecret())) {
            throw new IllegalStateException("Cannot find existing secret for ldap auth!");
        }

        var secret = secrets.get(spec.getConnection().getAuth().getExistingSecret());
        result.put(BIND_CRED, List.of(secret.get(spec.getConnection().getAuth().getPasswordKey())));
        result.put(BIND_DN, List.of(secret.get(spec.getConnection().getAuth().getDnKey())));
    }

    private ComponentRepresentation build() {
        return target;
    }
}

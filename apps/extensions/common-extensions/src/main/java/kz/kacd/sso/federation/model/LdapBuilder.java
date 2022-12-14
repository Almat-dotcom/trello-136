package kz.kacd.sso.federation.model;

import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.secret.Secret;
import kz.kacd.sso.k8s.secret.SecretValueProvider;
import kz.kacd.sso.v1.federationspec.Ldap;
import kz.kacd.sso.v1.federationspec.ldap.*;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;

import java.util.Collections;

import static kz.kacd.sso.util.ValueUtils.defaulted;
import static kz.kacd.sso.util.ValueUtils.defaultedPeriod;

public class LdapBuilder {
    public static final String LDAP_PROVIDER_ID = "ldap";
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

    private final String realmId;
    private final KeycloakSession session;

    private final ComponentModel target = new ComponentModel();

    public LdapBuilder(String realmId, KeycloakSession session) {
        this.realmId = realmId;
        this.session = session;
    }

    public ComponentModel buildFrom(
            Ldap spec
    ) {
        if (spec == null) {
            return build();
        }

        return withLdap(spec)
                .build();
    }

    private LdapBuilder withLdap(Ldap spec) {
        if (spec == null) {
            return this;
        }

        target.setParentId(realmId);
        target.setName(spec.getDisplayedName());
        target.setProviderId(LDAP_PROVIDER_ID);
        target.setProviderType(LDAP_PROVIDER_TYPE);
        target.setConfig(ldapConfig(spec));
        return this;
    }

    private MultivaluedHashMap<String, String> ldapConfig(Ldap spec) {
        MultivaluedHashMap<String, String> result = new MultivaluedHashMap<>();
        result.put(ALLOW_KERBEROS_AUTH, Collections.singletonList(Boolean.FALSE.toString()));
        bindCreds(result, spec);
        bindCache(result, spec.getCache());
        bindConnection(result, spec.getConnection());
        bindSync(result, spec.getSync());
        bindSearching(result, spec.getSearching());
        result.put(ENABLED, Collections.singletonList("true"));
        result.put(
                TRUST_EMAIL,
                Collections.singletonList(defaulted(spec.getAdvanced().getTrustEmail(), true).toString())
        );
        result.put(KERBEROS_FOR_PASSWORD, Collections.singletonList(Boolean.FALSE.toString()));
        result.put(LDAP3, Collections.singletonList(Boolean.FALSE.toString()));
        result.put(
                VALIDATE_PASSWORD,
                Collections.singletonList(defaulted(spec.getAdvanced().getValidatePassword(), false).toString())
        );
        result.put(VENDOR, Collections.singletonList(vendors(defaulted(spec.getVendor(), Ldap.Vendor.ACTIVE_DIRECTORY))));
        return result;
    }

    private String authType(Auth.Type type) {
        if (type == Auth.Type.SIMPLE) {
            return "simple";
        } else {
            return "none";
        }
    }

    private String vendors(Ldap.Vendor vendor) {
        if (vendor == Ldap.Vendor.ACTIVE_DIRECTORY) {
            return "ad";
        }
        return null;
    }

    private void bindCreds(MultivaluedHashMap<String, String> result, Ldap spec) {
        result.put(
                AUTH_TYPE,
                Collections.singletonList(
                        authType(defaulted(spec.getAuth().getType(), Auth.Type.SIMPLE))
                )
        );

        Secret secret = null;

        if (K8sConfig.ENABLED && spec.getAuth().getExistingSecret() != null) {
            secret = session.getProvider(SecretValueProvider.class).findByName(spec.getAuth().getExistingSecret());
        }

        if (spec.getAuth().getDnPlainValue() != null) {
            result.put(BIND_DN, Collections.singletonList(spec.getAuth().getDnPlainValue()));
        } else {
            if (secret == null) {
                throw new IllegalStateException("Cannot find existing secret!");
            }
            result.put(BIND_DN, Collections.singletonList(secret.get(spec.getAuth().getDnKey())));
        }

        if (spec.getAuth().getPasswordPlainValue() != null) {
            result.put(BIND_CRED, Collections.singletonList(spec.getAuth().getPasswordPlainValue()));
        } else {
            if (secret == null) {
                throw new IllegalStateException("Cannot find existing secret!");
            }
            result.put(BIND_CRED, Collections.singletonList(secret.get(spec.getAuth().getPasswordKey())));
        }
    }

    private void bindCache(MultivaluedHashMap<String, String> result, Cache cache) {
        Cache spec = cache;
        if (spec == null) {
            spec = new Cache();
        }

        result.put(
                CACHE_POLICY,
                Collections.singletonList(
                        defaulted(spec.getPolicy(), Cache.Policy.DEFAULT).name()
                )
        );
    }

    private void bindConnection(MultivaluedHashMap<String, String> result, Connection connection) {
        Connection spec = connection;
        if (spec == null) {
            spec = new Connection();
        }

        result.put(
                CONNECTION_POOLING,
                Collections.singletonList(defaulted(spec.getConnectionPooling(), false).toString())
        );
        result.put(CONNECTION_URL, Collections.singletonList(spec.getUrl()));
        result.put(START_TLS, Collections.singletonList(defaulted(spec.getStartTls(), false).toString()));

        result.put(
                USER_TRUSTSTORE_SPI,
                Collections.singletonList(
                        useTrustStore(defaulted(
                                spec.getUseTrustStore(),
                                Connection.UseTrustStore.LDAPS_ONLY
                        ))
                )
        );
    }

    private void bindSync(MultivaluedHashMap<String, String> result, Sync sync) {
        Sync spec = sync;
        if (spec == null) {
            spec = new Sync();
        }

        result.put(BATCH_SIZE, Collections.singletonList(defaulted(spec.getBatch(), 1_000).toString()));
        result.put(
                CHANGED_SYNC_PERIOD,
                Collections.singletonList(
                        defaultedPeriod(
                                defaulted(spec.getChangedSync(), false),
                                spec.getChangedSyncPeriod(),
                                "1m"
                        )
                )
        );
        result.put(
                FULL_SYNC_PERIOD,
                Collections.singletonList(
                        defaultedPeriod(
                                defaulted(spec.getFullSync(), false),
                                spec.getFullSyncPeriod(),
                                "1m"
                        )
                )
        );
        result.put(IMPORT_ENABLED, Collections.singletonList(defaulted(spec.getImportUsers(), true).toString()));
        result.put(
                SYNC_REGISTRATIONS,
                Collections.singletonList(defaulted(spec.getSyncRegistrations(), false).toString())
        );
    }

    private String useTrustStore(Connection.UseTrustStore source) {
        switch (source) {
            case NEVER:
                return "never";
            case ALWAYS:
                return "always";
            case LDAPS_ONLY:
                return "ldapsOnly";
        }
        return null;
    }

    private void bindSearching(MultivaluedHashMap<String, String> result, Searching searching) {
        Searching spec = searching;
        if (spec == null) {
            spec = new Searching();
        }

        result.put(
                EDIT_MODE,
                Collections.singletonList(
                        searchMode(defaulted(spec.getMode(), Searching.Mode.R))
                )
        );

        result.put(
                PAGINATION,
                Collections.singletonList(defaulted(spec.getPagination(), false).toString())
        );
        result.put(RDN_LDAP_ATTR, Collections.singletonList(defaulted(spec.getUsername(), "cn")));
        result.put(
                SEARCH_SCOPE,
                Collections.singletonList(
                        scopes(defaulted(spec.getScope(), Searching.Scope.FLAT))
                )
        );
        result.put(USER_CLASSES, Collections.singletonList(defaulted(spec.getClasses(), DEFAULT_USER_CLASSES)));
        result.put(USERNAME_ATTR, Collections.singletonList(defaulted(spec.getUsername(), "cn")));
        result.put(USERS_DN, Collections.singletonList(spec.getUsersDn()));
        result.put(UUID_ATTR, Collections.singletonList(defaulted(spec.getUuid(), "cn")));

    }

    private String searchMode(Searching.Mode mode) {
        if (mode == Searching.Mode.R) {
            return "READ_ONLY";
        } else if (mode == Searching.Mode.RW) {
            return "WRITABLE";
        }
        return null;
    }

    private String scopes(Searching.Scope scope) {
        if (scope == Searching.Scope.FLAT) {
            return "1";
        } else if (scope == Searching.Scope.SUBTREE) {
            return "2";
        }
        return null;
    }

    private ComponentModel build() {
        return target;
    }
}

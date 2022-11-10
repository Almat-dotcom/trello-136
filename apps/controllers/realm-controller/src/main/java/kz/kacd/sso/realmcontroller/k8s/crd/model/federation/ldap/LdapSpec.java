package kz.kacd.sso.realmcontroller.k8s.crd.model.federation.ldap;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.Getter;

@Data
public class LdapSpec {

    @JsonPropertyDescription("Displayed name in admin console")
    private String displayedName;
    @JsonPropertyDescription("Name of the vendor of ldap users")
    private LdapVendors vendor;
    @JsonPropertyDescription("Ldap connection settings")
    private LdapConnectionSpec connection;
    @JsonPropertyDescription("Properties of searching users in LDAP")
    private LdapSearchingSpec searching;
    @JsonPropertyDescription("Synchronizing settings")
    private LdapSyncSpec sync;
    @JsonPropertyDescription("Cache settings")
    private LdapCacheSpec cache;
    @JsonPropertyDescription("Advanced settings")
    private LdapAdvancedSpec advanced;
    @JsonPropertyDescription("Groups mapping settings")
    private LdapGroupsMappingSpec groups;

    @Getter
    public enum LdapVendors {

        ACTIVE_DIRECTORY("ad");

        private final String code;

        LdapVendors(String code) {
            this.code = code;
        }
    }
}

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

    @Getter
    public enum LdapVendors {

        ACTIVE_DIRECTORY("ad");

        private final String code;

        LdapVendors(String code) {
            this.code = code;
        }
    }
}

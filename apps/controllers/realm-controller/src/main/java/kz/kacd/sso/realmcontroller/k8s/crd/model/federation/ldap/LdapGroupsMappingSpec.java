package kz.kacd.sso.realmcontroller.k8s.crd.model.federation.ldap;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class LdapGroupsMappingSpec {

    @JsonPropertyDescription("DN of UO where all groups are stored.")
    private String dn;
    @JsonPropertyDescription("Name of the groups name attribute (default cn)")
    private String name;
    @JsonPropertyDescription("If true hierarchy of the groups in ldap will be saved in keycloak.")
    private Boolean hierarchy;
    @JsonPropertyDescription("If true all missing groups in hierarchy will be ignored")
    private Boolean ignoreMissing;
    @JsonPropertyDescription("Name of the membership attribute in user representation")
    private String attributeName;
    @JsonPropertyDescription("Additional filter of groups in ldap")
    private String filter;
    @JsonPropertyDescription(
            "Mode of the groups management. " +
                    "By default R - it means read only mode. " +
                    "RW - all groups mapping will be retrieved from LDAP and stored in LDAP. " +
                    "RWC - import groups mapping into keycloak DB and manages it in DB."
    )
    private Modes mode;
    @JsonPropertyDescription("If true all non-existing in LDAP groups will be dropped.")
    private Boolean dropNonExisting;
    @JsonPropertyDescription("Base path of groups hierarchy in keycloak. By default /")
    private String path;

    @RequiredArgsConstructor
    @Getter
    public enum Modes {

        R("READ_ONLY"),
        RW("LDAP_ONLY"),
        RWC("IMPORT");

        private final String code;
    }
}

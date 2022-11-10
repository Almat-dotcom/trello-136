package kz.kacd.sso.realmcontroller.k8s.crd.model.federation.ldap;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.Getter;

@Data
public class LdapConnectionSpec {

    @JsonPropertyDescription("Full specified url to connect to ldap. E.g. ldaps://mydomain:689")
    private String url;
    @JsonPropertyDescription("If true start TLS will be used to encrypt messages")
    private Boolean startTls;
    @JsonPropertyDescription(
            "Mode of using Java trust store. " +
                    "LDAPS_ONLY is default and means that only with ldaps protocol trust store will be used. " +
                    "ALWAYS means that every protocols will be used with trust store. " +
                    "NONE means that trust store will not be used at all."
    )
    private UseTrustStoreModes useTrustStore;
    @JsonPropertyDescription("If true connection pooling will be used to access LDAP server")
    private Boolean connectionPooling;
    @JsonPropertyDescription("Duration of connection timeout (duration can be s(Seconds) m(Minutest), h(Hours), d(Days))")
    private String connectionTimeout;
    @JsonPropertyDescription("Authentication properties of LDAP admin")
    private LdapAuthSpec auth;

    @Getter
    public enum UseTrustStoreModes {

        LDAPS_ONLY("ldapsOnly"),
        ALWAYS("always"),
        NEVER("never");

        private final String code;

        UseTrustStoreModes(String code) {
            this.code = code;
        }
    }
}

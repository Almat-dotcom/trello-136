package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.ldap;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class LdapSearchingSpec {

    @JsonPropertyDescription(
            "Mode of access users in LDAP. " +
                    "By default is R - read only access. " +
                    "RW - read and write access; allows admins edit users using keycloak admin console."
    )
    private Modes mode;
    @JsonPropertyDescription("Describes full DN of UO where all users is stored.")
    private String usersDn;
    @JsonPropertyDescription("Describes name of the username attribute")
    private String username;
    @JsonPropertyDescription("Describes name of the UUID attribute")
    private String uuid;
    @JsonPropertyDescription("List comma and space separated of user classes. By default person, organizationalPerson, user")
    private String classes;
    @JsonPropertyDescription("Additional LDAP filter on users")
    private String filter;
    @JsonPropertyDescription(
            "Scope of the searching. By default it is SUBTREE - search all sub UO for users. " +
                    "FLAT - search only in the first level of UO."
    )
    private Scopes scope;
    @JsonPropertyDescription(
            "Timeout of the read of users " +
                    "(duration can be s(Seconds) m(Minutest), h(Hours), d(Days))"
    )
    private String readTimeout;
    @JsonPropertyDescription("If true keycloak will be used pagination. Make sure that your LDAP support this function")
    private Boolean pagination;

    @RequiredArgsConstructor
    @Getter
    public enum Modes {

        R("READ_ONLY"),
        RW("WRITABLE");

        private final String code;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Scopes {

        SUBTREE("2"),
        FLAT("1");

        private final String code;
    }
}

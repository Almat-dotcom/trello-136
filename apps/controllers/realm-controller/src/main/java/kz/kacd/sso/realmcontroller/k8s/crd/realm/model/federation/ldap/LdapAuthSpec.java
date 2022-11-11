package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.ldap;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.Getter;

@Data
public class LdapAuthSpec {

    @JsonPropertyDescription(
            "Type of credentials binding. By default this is simple binding."
    )
    private LdapAuthBindings type;
    @JsonPropertyDescription("Name of the existing simple which contains auth DN and password")
    private String existingSecret;
    @JsonPropertyDescription("Key of the authentication DN inside existing secret")
    private String dnKey;
    @JsonPropertyDescription("Key of the authentication password inide existing secret")
    private String passwordKey;

    @Getter
    public enum LdapAuthBindings {

        SIMPLE("simple"),
        NONE("none");

        private final String code;

        LdapAuthBindings(String code) {
            this.code = code;
        }
    }
}

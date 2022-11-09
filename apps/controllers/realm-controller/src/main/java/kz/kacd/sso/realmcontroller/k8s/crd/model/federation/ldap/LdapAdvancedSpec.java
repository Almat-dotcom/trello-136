package kz.kacd.sso.realmcontroller.k8s.crd.model.federation.ldap;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class LdapAdvancedSpec {

    @JsonPropertyDescription("If true keycloak password verification policy will be applied on ldap users.")
    private Boolean validatePassword;
    @JsonPropertyDescription("If true all emails in ldap will be trusted by keycloak")
    private Boolean trustEmail;
}

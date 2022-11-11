package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.ldap.LdapSpec;
import lombok.Data;

@Data
public class FederationSpec {

    @JsonPropertyDescription("Configuration of ldap federation")
    private LdapSpec ldap;
}

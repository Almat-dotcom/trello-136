package kz.kacd.sso.realmcontroller.k8s.crd.client.model.ldap;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class ClientLdapSpec {

    @JsonPropertyDescription("Full specified DN name of the roles specific for this client")
    private String dn;
}

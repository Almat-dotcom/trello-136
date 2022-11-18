package kz.kacd.sso.realmcontroller.k8s.crd.client.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.access.ClientAccessSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.capability.CapabilitySpec;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.ldap.ClientLdapSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.role.ClientRole;
import lombok.Data;

import java.util.List;

@Data
public class ClientSpec {

    @JsonPropertyDescription("Name of the realm")
    private String realm;
    @JsonPropertyDescription("Name to be displayed in admin console")
    private String displayedName;
    @JsonPropertyDescription("Full specified description of the client. Can be multiline string")
    private String description;
    @JsonPropertyDescription("Settings of the access of client")
    private ClientAccessSpec access;
    @JsonPropertyDescription("Setting of client capabilities")
    private CapabilitySpec capability;
    @JsonPropertyDescription("Settings of the ldap roles mappings if ldap is configured on this realm")
    private ClientLdapSpec ldap;
    @JsonPropertyDescription(
            "List of the attributes which will be additional mapped to id token and userinfo."
    )
    private List<AdditionalAttributes> attributes;
    @JsonPropertyDescription(
            "List of the roles which defined inside client."
    )
    private List<ClientRole> roles;
    @JsonPropertyDescription("Strict authentication only for users in group")
    private String allowedGroup;

    public enum AdditionalAttributes {
        FIRST_NAME,
        LAST_NAME,
        MIDDLE_NAME,
        GROUPS,
        LOCALE
    }
}

package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.ldap;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class LdapSyncSpec {

    @JsonPropertyDescription("If true users will be imported into keycloak DB")
    private Boolean importUsers;
    @JsonPropertyDescription("If true all registrations will be persisted in LDAP")
    private Boolean syncRegistrations;
    @JsonPropertyDescription("Size of one transaction batch of users")
    private Long batch;
    @JsonPropertyDescription("If true full sync will be periodically ran")
    private Boolean fullSync;
    @JsonPropertyDescription("Period of full sync (duration can be s(Seconds) m(Minutest), h(Hours), d(Days))")
    private String fullSyncPeriod;
    @JsonPropertyDescription("If true changed users will be synced periodically")
    private Boolean changedSync;
    @JsonPropertyDescription("Period of changed sync (duration can be s(Seconds) m(Minutest), h(Hours), d(Days))")
    private String changedSyncPeriod;
}

package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.ldap;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class LdapCacheSpec {

    @JsonPropertyDescription(
            "Mode of the cache. " +
                    "By default it is DEFAULT - global cache settings. " +
                    "EVICT_DAILY - every day cache will be evicted. " +
                    "EVICT_WEEKLY - every week cache will be evicted."
    )
    private Policies policy;

    public enum Policies {
        DEFAULT, EVICT_DAILY, EVICT_WEEKLY
    }
}

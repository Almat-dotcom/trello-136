package kz.kacd.sso.realmcontroller.k8s.crd.realm.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealmStatus {

    @JsonPropertyDescription("State of the realm application")
    private RealmState state;
    @JsonPropertyDescription("Additional message for state")
    private String message;
    @JsonPropertyDescription("Applied generation")
    private String generation;

    public enum RealmState {
        /**
         * Realm config has been detected by controller
         */
        DETECTED,
        /**
         * Realm config is processing
         */
        APPLYING,
        /**
         * Realm config applied to keycloak server
         */
        APPLIED,
        /**
         * Realm config application has been failed
         */
        FAILED
    }
}

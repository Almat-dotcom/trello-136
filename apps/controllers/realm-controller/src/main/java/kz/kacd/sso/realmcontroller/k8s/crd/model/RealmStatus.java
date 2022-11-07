package kz.kacd.sso.realmcontroller.k8s.crd.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class RealmStatus {

    @JsonPropertyDescription("State of the realm application")
    private RealmState error;
    @JsonPropertyDescription("Additional message for state")
    private String message;

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

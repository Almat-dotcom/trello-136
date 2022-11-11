package kz.kacd.sso.realmcontroller.k8s.crd.client.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientStatus {

    @JsonPropertyDescription("State of the client")
    private State state;
    @JsonPropertyDescription("Description of the state")
    private String message;
    @JsonPropertyDescription("Last applied generation")
    private Integer generation;
    @JsonPropertyDescription("Time of the last try to apply client")
    private String lastApplication;
    @JsonPropertyDescription("Time to backoff in seconds")
    private Integer backoffSeconds;

    public enum State {
        /**
         * Client now in process to be applied on realm.
         */
        APPLYING,
        /**
         * Client has been successfully applied.
         */
        APPLIED,
        /**
         * Failed to apply client into realm.
         */
        FAILED,
        /**
         * Client now in backoff to be applied.
         */
        BACKOFF,
        /**
         * Waiting for realm to be applied.
         */
        WAITING_FOR_REALM
    }
}

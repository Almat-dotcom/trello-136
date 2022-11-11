package kz.kacd.sso.realmcontroller.k8s.crd.realm.model.session;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class SessionsSpec {

    @JsonPropertyDescription("Session expiration if no refresh actions requested")
    private String sessionIdle;
    @JsonPropertyDescription("Maximum lifetime of the session")
    private String sessionMax;
    @JsonPropertyDescription("Offline session lifetime when no refresh actions requested")
    private String offlineSessionIdle;
}

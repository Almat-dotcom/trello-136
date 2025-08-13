package kz.kacd.sso.external.v1;

import java.time.LocalDateTime;

public class ClientStatus {
    private State state;
    private String message;
    private LocalDateTime lastTransitionTime;
    private String error;

    public enum State {
        APPLIED,
        FAILED,
        BACKOFF,
        WAITING_FOR_REALM,
        APPLYING
    }

    public ClientStatus() {
    }

    public ClientStatus(State state) {
        this.state = state;
        this.lastTransitionTime = LocalDateTime.now();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        this.lastTransitionTime = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getLastTransitionTime() {
        return lastTransitionTime;
    }

    public void setLastTransitionTime(LocalDateTime lastTransitionTime) {
        this.lastTransitionTime = lastTransitionTime;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

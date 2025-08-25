package kz.kacd.sso.v1;

public class ClientStatus {
    public enum State {
        APPLYING, APPLIED, FAILED, BACKOFF
    }
    
    private State state;
    private String lastApplication;
    private String error;
    
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    
    public String getLastApplication() { return lastApplication; }
    public void setLastApplication(String lastApplication) { this.lastApplication = lastApplication; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}


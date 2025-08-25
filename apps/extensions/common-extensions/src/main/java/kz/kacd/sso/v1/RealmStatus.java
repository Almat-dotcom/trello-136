package kz.kacd.sso.v1;

public class RealmStatus {
    public enum State {
        APPLYING, APPLIED, BACK_OFF, FAILED
    }
    
    private State state;
    private String message;
    private String generation;
    private String lastApplication;
    
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getGeneration() { return generation; }
    public void setGeneration(String generation) { this.generation = generation; }
    
    public String getLastApplication() { return lastApplication; }
    public void setLastApplication(String lastApplication) { this.lastApplication = lastApplication; }
}



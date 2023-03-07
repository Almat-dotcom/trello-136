package kz.kacd.sso.redis.sessions.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonSessionEntity {

    private String clientId;
    private String authUserId;
    private int timestamp = 0;
    private String redirectUri;
    private String action;
    private List<String> clientScopes = new ArrayList<>();
    private Map<String, String> executionStatus = new ConcurrentHashMap<>();
    private String protocol;
    private Map<String, String> clientNotes = new ConcurrentHashMap<>();
    private Map<String, String> authNotes = new ConcurrentHashMap<>();
    private List<String> requiredActions = new ArrayList<>();
    private Map<String, String> userSessionNotes = new ConcurrentHashMap<>();

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getClientScopes() {
        return clientScopes;
    }

    public void setClientScopes(List<String> clientScopes) {
        this.clientScopes = clientScopes;
    }

    public Map<String, String> getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(Map<String, String> executionStatus) {
        this.executionStatus = executionStatus;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getClientNotes() {
        return clientNotes;
    }

    public void setClientNotes(Map<String, String> clientNotes) {
        this.clientNotes = clientNotes;
    }

    public Map<String, String> getAuthNotes() {
        return authNotes;
    }

    public void setAuthNotes(Map<String, String> authNotes) {
        this.authNotes = authNotes;
    }

    public List<String> getRequiredActions() {
        return requiredActions;
    }

    public void setRequiredActions(List<String> requiredActions) {
        this.requiredActions = requiredActions;
    }

    public Map<String, String> getUserSessionNotes() {
        return userSessionNotes;
    }

    public void setUserSessionNotes(Map<String, String> userSessionNotes) {
        this.userSessionNotes = userSessionNotes;
    }
}

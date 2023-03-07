package kz.kacd.sso.redis.sessions;

import kz.kacd.sso.redis.sessions.entities.CommonSessionEntity;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisAuthenticationSession implements AuthenticationSessionModel {

    private final String tabId;
    private final RedisRootAuthenticationSession root;
    private final RealmModel realm;
    private final ClientModel client;
    private UserModel user;
    private final CommonSessionEntity entity;
    private final SessionManager manager;

    public RedisAuthenticationSession(
            String tabId,
            RedisRootAuthenticationSession root,
            RealmModel realm,
            ClientModel client,
            UserModel user,
            CommonSessionEntity entity,
            SessionManager manager
    ) {
        this.tabId = tabId;
        this.root = root;
        this.realm = realm;
        this.client = client;
        this.user = user;
        this.entity = entity;
        this.manager = manager;
    }

    @Override
    public String getTabId() {
        return tabId;
    }

    @Override
    public RootAuthenticationSessionModel getParentSession() {
        return root;
    }

    @Override
    public Map<String, ExecutionStatus> getExecutionStatus() {
        Map<String, ExecutionStatus> result = new HashMap<>();
        entity.getExecutionStatus().forEach((k, v) -> result.put(k, ExecutionStatus.valueOf(v)));
        return result;
    }

    @Override
    public void setExecutionStatus(String authenticator, ExecutionStatus status) {
        entity.getExecutionStatus().put(authenticator, status.name());
        save();
    }

    @Override
    public void clearExecutionStatus() {
        entity.getExecutionStatus().clear();
        save();
    }

    @Override
    public UserModel getAuthenticatedUser() {
        return user;
    }

    @Override
    public void setAuthenticatedUser(UserModel user) {
        this.user = user;
        entity.setAuthUserId(user.getId());
        save();
    }

    @Override
    public Set<String> getRequiredActions() {
        return new HashSet<>(entity.getRequiredActions());
    }

    @Override
    public void addRequiredAction(UserModel.RequiredAction action) {
        addRequiredAction(action.name());
    }

    @Override
    public void addRequiredAction(String action) {
        if (!entity.getRequiredActions().contains(action)) {
            entity.getRequiredActions().add(action);
            save();
        }
    }

    @Override
    public void removeRequiredAction(UserModel.RequiredAction action) {
        removeRequiredAction(action.name());
    }

    @Override
    public void removeRequiredAction(String action) {
        entity.getRequiredActions().remove(action);
        save();
    }

    @Override
    public void setUserSessionNote(String name, String value) {
        entity.getUserSessionNotes().put(name, value);
        save();
    }

    @Override
    public Map<String, String> getUserSessionNotes() {
        return new HashMap<>(entity.getUserSessionNotes());
    }

    @Override
    public void clearUserSessionNotes() {
        entity.getUserSessionNotes().clear();
        save();
    }

    @Override
    public String getAuthNote(String name) {
        return entity.getAuthNotes().get(name);
    }

    @Override
    public void setAuthNote(String name, String value) {
        entity.getAuthNotes().put(name, value);
        save();
    }

    @Override
    public void removeAuthNote(String name) {
        entity.getAuthNotes().remove(name);
        save();
    }

    @Override
    public void clearAuthNotes() {
        entity.getAuthNotes().clear();
        save();
    }

    @Override
    public String getClientNote(String name) {
        return entity.getClientNotes().get(name);
    }

    @Override
    public void setClientNote(String name, String value) {
        entity.getClientNotes().put(name, value);
        save();
    }

    @Override
    public void removeClientNote(String name) {
        entity.getClientNotes().remove(name);
        save();
    }

    @Override
    public Map<String, String> getClientNotes() {
        return new HashMap<>(entity.getClientNotes());
    }

    @Override
    public void clearClientNotes() {
        entity.getClientNotes().clear();
        save();
    }

    @Override
    public Set<String> getClientScopes() {
        return new HashSet<>(entity.getClientScopes());
    }

    @Override
    public void setClientScopes(Set<String> clientScopes) {
        entity.getClientScopes().addAll(clientScopes);
        save();
    }

    @Override
    public String getRedirectUri() {
        return entity.getRedirectUri();
    }

    @Override
    public void setRedirectUri(String uri) {
        entity.setRedirectUri(uri);
        save();
    }

    @Override
    public RealmModel getRealm() {
        return realm;
    }

    @Override
    public ClientModel getClient() {
        return client;
    }

    @Override
    public String getAction() {
        return entity.getAction();
    }

    @Override
    public void setAction(String action) {
        entity.setAction(action);
        save();
    }

    @Override
    public String getProtocol() {
        return entity.getProtocol();
    }

    @Override
    public void setProtocol(String method) {
        entity.setProtocol(method);
        save();
    }

    private void save() {
        manager.save();
    }
}

package kz.kacd.sso.redis.sessions.entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RootSessionEntity {

    private String id;
    private String realmId;
    private int timestamp = 0;
    private Map<String, CommonSessionEntity> children = new ConcurrentHashMap<>();

    public RootSessionEntity() {
    }

    public RootSessionEntity(String id) {
        this.id = id;
    }

    public RootSessionEntity(String id, String realmId, int timestamp) {
        this.id = id;
        this.realmId = realmId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealmId() {
        return realmId;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, CommonSessionEntity> getChildren() {
        return children;
    }

    public void setChildren(Map<String, CommonSessionEntity> children) {
        this.children = children;
    }
}

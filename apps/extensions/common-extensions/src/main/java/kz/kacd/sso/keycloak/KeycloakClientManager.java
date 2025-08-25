package kz.kacd.sso.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

public class KeycloakClientManager {
    private static final Logger log = Logger.getLogger(KeycloakClientManager.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ClientModel createClient(KeycloakSession session, String clientId, String realmName) {
        try {
            log.infof("Creating client '%s' in realm '%s'", clientId, realmName);
            
            // Get or create realm
            RealmModel realm = session.realms().getRealmByName(realmName);
            if (realm == null) {
                log.warnf("Realm '%s' not found, creating it", realmName);
                realm = createRealm(session, realmName);
            }
            
            // Check if client already exists
            ClientModel existingClient = session.clients().getClientByClientId(realm, clientId);
            if (existingClient != null) {
                log.infof("Client '%s' already exists in realm '%s'", clientId, realmName);
                return existingClient;
            }
            
            // Create new client
            ClientModel client = session.clients().addClient(realm, clientId);
            
            // Configure client
            client.setClientId(clientId);
            client.setName(clientId);
            client.setEnabled(true);
            client.setPublicClient(true);
            client.setDirectAccessGrantsEnabled(true);
            client.setStandardFlowEnabled(true);
            
            // Set redirect URIs
            client.setRedirectUris(new HashSet<>(Arrays.asList("http://localhost:3000/*", "http://localhost:3000")));
            client.setWebOrigins(new HashSet<>(Arrays.asList("http://localhost:3000")));
            
            log.infof("Successfully created client '%s' in realm '%s'", clientId, realmName);
            return client;
            
        } catch (Exception e) {
            log.errorf(e, "Error creating client '%s' in realm '%s'", clientId, realmName);
            throw new RuntimeException("Failed to create client", e);
        }
    }
    
    private static RealmModel createRealm(KeycloakSession session, String realmName) {
        try {
            log.infof("Creating realm '%s'", realmName);
            
            RealmManager realmManager = new RealmManager(session);
            RealmModel realm = realmManager.createRealm(realmName, realmName);
            
            // Set realm as enabled
            realm.setEnabled(true);
            realm.setDisplayName(realmName);
            realm.setDisplayNameHtml("<div class=\"kc-logo-text\"><span>" + realmName + "</span></div>");
            
            log.infof("Successfully created realm '%s'", realmName);
            return realm;
            
        } catch (Exception e) {
            log.errorf(e, "Error creating realm '%s'", realmName);
            throw new RuntimeException("Failed to create realm", e);
        }
    }
    
    public static Map<String, Object> getClientInfo(ClientModel client) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", client.getId());
        info.put("clientId", client.getClientId());
        info.put("name", client.getName());
        info.put("enabled", client.isEnabled());
        info.put("publicClient", client.isPublicClient());
        info.put("redirectUris", client.getRedirectUris());
        info.put("webOrigins", client.getWebOrigins());
        return info;
    }
}

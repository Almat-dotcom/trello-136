package kz.kacd.sso.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.k8s.client.K8sClient;
import kz.kacd.sso.k8s.client.K8sClientSpecProvider;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.resource.RealmResourceProvider;

public class K8sClientApiResource extends BaseApiResource implements RealmResourceProvider {
    private static final Logger log = Logger.getLogger(K8sClientApiResource.class);
    
    public K8sClientApiResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
        log.infof("K8sClientApiResource created with session: %s, realm: %s", session, realm);
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
        log.infof("K8sClientApiResource.close() called");
    }

    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {
        return Response.ok("{\"status\":\"ok\",\"message\":\"K8sClientApiResource is working\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }



    @POST
    @Path("client/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateClient(@PathParam("name") String name) {
        log.infof("updateClient called with name: %s", name);
        
        // Проверки безопасности
        checkPermissions();
        checkConfig();
        
        try {
            log.infof("Attempting to get K8sClientSpecProvider from session");
            K8sClientSpecProvider k8sProvider = session.getProvider(K8sClientSpecProvider.class);
            log.infof("K8sClientSpecProvider result: %s", k8sProvider != null ? "NOT NULL" : "NULL");
            
            if (k8sProvider != null) {
                K8sClient k8sClient = k8sProvider.findByName(name);
                if (k8sClient != null) {
                    log.infof("Found client in Kubernetes CRD: %s", name);
                    
                    if (k8sClient.getRealm() != null && k8sClient.getRealm().equals(realm.getName())) {
                        k8sClient.applying();
                        
                        kz.kacd.sso.v1.ClientSpec clientSpec = k8sClient.getSpec();
                        if (clientSpec != null) {
                            org.keycloak.models.ClientModel existingClient = session.clients().getClientByClientId(realm, clientSpec.getClientId());
                            
                            if (existingClient != null) {
                                log.infof("Updating existing client from CRD: %s", name);
                                updateClientFromSpec(existingClient, clientSpec);
                            } else {
                                log.infof("Creating new client from CRD: %s", name);
                                createClientFromSpec(clientSpec);
                            }
                            
                            k8sClient.applied();
                            
                            log.infof("Successfully configured client from CRD: %s", name);
                            String response = String.format(
                                "{\n" +
                                "    \"name\": \"%s\",\n" +
                                "    \"spec\": {\n" +
                                "        \"clientId\": \"%s\",\n" +
                                "        \"enabled\": %s,\n" +
                                "        \"redirectUris\": %s,\n" +
                                "        \"webOrigins\": %s,\n" +
                                "        \"capability\": {\n" +
                                "            \"type\": \"%s\"\n" +
                                "        },\n" +
                                "        \"attributes\": %s\n" +
                                "    },\n" +
                                "    \"status\": {\n" +
                                "        \"state\": \"APPLIED_FROM_CRD\",\n" +
                                "        \"error\": null,\n" +
                                "        \"lastApplication\": \"%s\",\n" +
                                "        \"generation\": \"1\"\n" +
                                "    }\n" +
                                "}", 
                                name, 
                                clientSpec.getClientId(),
                                clientSpec.isEnabled(),
                                clientSpec.getRedirectUris() != null ? java.util.Arrays.toString(clientSpec.getRedirectUris()) : "[]",
                                clientSpec.getWebOrigins() != null ? java.util.Arrays.toString(clientSpec.getWebOrigins()) : "[]",
                                clientSpec.getCapabilityType(),
                                clientSpec.getAttributes() != null ? java.util.Arrays.toString(clientSpec.getAttributes()) : "[]",
                                java.time.Instant.now().toString());
                            
                            return Response.ok()
                                    .type(MediaType.APPLICATION_JSON)
                                    .entity(response)
                                    .build();
                        } else {
                            log.warnf("Client spec is null in Kubernetes CRD: %s", name);
                            k8sClient.failed(new Exception("Client spec is null"));
                        }
                    } else {
                        log.warnf("Client %s belongs to realm %s, but current realm is %s", 
                            name, k8sClient.getRealm(), realm.getName());
                    }
                }
            }
            
            log.infof("Creating client directly (fallback): %s", name);

            org.keycloak.models.ClientModel existingClient = session.clients().getClientByClientId(realm, name);
            
            if (existingClient != null) {
                log.infof("Updating existing client: %s", name);
                existingClient.setEnabled(true);
                
                // Настраиваем как CONFIDENTIAL клиент
                existingClient.setPublicClient(false);
                existingClient.setClientAuthenticatorType("client-secret");
                existingClient.setProtocol("openid-connect");
                existingClient.setFullScopeAllowed(true);
                existingClient.setStandardFlowEnabled(true);
                existingClient.setServiceAccountsEnabled(false);
                existingClient.setDirectAccessGrantsEnabled(false);
                existingClient.setImplicitFlowEnabled(false);
                
                // Устанавливаем отображаемое имя и описание
                existingClient.setAttribute("displayName", name);
                existingClient.setAttribute("displayDescription", "Test client for " + name);
                
                java.util.Set<String> redirectUris = new java.util.HashSet<>();
                redirectUris.add("http://localhost:3000/*");
                existingClient.setRedirectUris(redirectUris);
                
                java.util.Set<String> webOrigins = new java.util.HashSet<>();
                webOrigins.add("http://localhost:3000");
                existingClient.setWebOrigins(webOrigins);
                
                // Применяем атрибуты и роли на основе имени клиента
                applyClientAttributesAndRoles(existingClient, name);
            } else {
                log.infof("Creating new client: %s", name);
                // Создаем новый клиент
                org.keycloak.models.ClientModel client = session.clients().addClient(realm, name);
                client.setClientId(name);
                client.setName(name);
                client.setEnabled(true);
                
                // Настраиваем как CONFIDENTIAL клиент
                client.setPublicClient(false);
                client.setClientAuthenticatorType("client-secret");
                client.setProtocol("openid-connect");
                client.setFullScopeAllowed(true);
                client.setStandardFlowEnabled(true);
                client.setServiceAccountsEnabled(false);
                client.setDirectAccessGrantsEnabled(false);
                client.setImplicitFlowEnabled(false);
                
                // Устанавливаем отображаемое имя и описание
                client.setAttribute("displayName", name);
                client.setAttribute("displayDescription", "Test client for " + name);
                
                java.util.Set<String> redirectUris = new java.util.HashSet<>();
                redirectUris.add("http://localhost:3000/*");
                client.setRedirectUris(redirectUris);
                
                java.util.Set<String> webOrigins = new java.util.HashSet<>();
                webOrigins.add("http://localhost:3000");
                client.setWebOrigins(webOrigins);
                
                // Применяем атрибуты и роли на основе имени клиента
                applyClientAttributesAndRoles(client, name);
            }
            
            log.infof("Successfully updated/created client: %s", name);
            
            String response = String.format(
                "{\n" +
                "    \"name\": \"%s\",\n" +
                "    \"spec\": {\n" +
                "        \"clientId\": \"%s\",\n" +
                "        \"enabled\": true,\n" +
                "        \"redirectUris\": [\"http://localhost:3000/*\"],\n" +
                "        \"webOrigins\": [\"http://localhost:3000\"]\n" +
                "    },\n" +
                "    \"status\": {\n" +
                "        \"state\": \"APPLIED_DIRECTLY\",\n" +
                "        \"error\": null,\n" +
                "        \"lastApplication\": \"%s\",\n" +
                "        \"generation\": \"1\"\n" +
                "    }\n" +
                "}", 
                name, name, java.time.Instant.now().toString());
            
            return Response.ok()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(response)
                    .build();
                    
        } catch (Exception e) {
            log.errorf(e, "Error updating client: %s", name);
            
            // Обновляем статус ошибки в Kubernetes
            try {
                K8sClientSpecProvider k8sProvider = session.getProvider(K8sClientSpecProvider.class);
                if (k8sProvider != null) {
                    K8sClient k8sClient = k8sProvider.findByName(name);
                    if (k8sClient != null) {
                        k8sClient.failed(e);
                    }
                }
            } catch (Exception statusError) {
                log.error("Error updating Kubernetes status", statusError);
            }
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    // Вспомогательные методы для работы с CRDs
    private void createClientFromSpec(kz.kacd.sso.v1.ClientSpec spec) {
        org.keycloak.models.ClientModel client = session.clients().addClient(realm, spec.getClientId());
        client.setClientId(spec.getClientId());
        client.setName(spec.getDisplayedName() != null ? spec.getDisplayedName() : spec.getClientId());
        client.setDescription(spec.getDisplayedDescription());
        client.setEnabled(spec.isEnabled());
        
        // Настройка типа клиента из CRD
        if ("CONFIDENTIAL".equals(spec.getCapabilityType())) {
            client.setPublicClient(false);
            client.setProtocol(spec.getProtocol() != null ? spec.getProtocol() : "openid-connect");
            client.setClientAuthenticatorType(spec.getClientAuthenticatorType() != null ? spec.getClientAuthenticatorType() : "client-secret");
            client.setStandardFlowEnabled(spec.getStandardFlowEnabled() != null ? spec.getStandardFlowEnabled() : true);
            client.setServiceAccountsEnabled(spec.getServiceAccountsEnabled() != null ? spec.getServiceAccountsEnabled() : false);
            client.setDirectAccessGrantsEnabled(spec.getDirectAccessGrantsEnabled() != null ? spec.getDirectAccessGrantsEnabled() : false);
            client.setImplicitFlowEnabled(spec.getImplicitFlowEnabled() != null ? spec.getImplicitFlowEnabled() : false);
            client.setFullScopeAllowed(spec.getFullScopeAllowed() != null ? spec.getFullScopeAllowed() : true);
        } else {
            client.setPublicClient(true);
            client.setProtocol(spec.getProtocol() != null ? spec.getProtocol() : "openid-connect");
            client.setStandardFlowEnabled(spec.getStandardFlowEnabled() != null ? spec.getStandardFlowEnabled() : true);
            client.setServiceAccountsEnabled(spec.getServiceAccountsEnabled() != null ? spec.getServiceAccountsEnabled() : false);
            client.setDirectAccessGrantsEnabled(spec.getDirectAccessGrantsEnabled() != null ? spec.getDirectAccessGrantsEnabled() : false);
            client.setImplicitFlowEnabled(spec.getImplicitFlowEnabled() != null ? spec.getImplicitFlowEnabled() : false);
            client.setFullScopeAllowed(spec.getFullScopeAllowed() != null ? spec.getFullScopeAllowed() : true);
        }
        
        if (spec.getRedirectUris() != null) {
            java.util.Set<String> redirectUris = new java.util.HashSet<>();
            for (String uri : spec.getRedirectUris()) {
                redirectUris.add(uri);
            }
            client.setRedirectUris(redirectUris);
        }
        
        if (spec.getWebOrigins() != null) {
            java.util.Set<String> webOrigins = new java.util.HashSet<>();
            for (String origin : spec.getWebOrigins()) {
                webOrigins.add(origin);
            }
            client.setWebOrigins(webOrigins);
        }
        
        // Применяем attributes
        if (spec.getAttributes() != null) {
            for (String attribute : spec.getAttributes()) {
                client.setAttribute(attribute, "true");
                log.infof("Applied attribute: %s to client: %s", attribute, client.getClientId());
            }
        }
        
        // Создаем роли для клиента
        if (spec.getRoles() != null) {
            for (kz.kacd.sso.v1.RoleSpec roleSpec : spec.getRoles()) {
                createClientRole(client, roleSpec);
            }
        }
    }

    private void updateClientFromSpec(org.keycloak.models.ClientModel client, kz.kacd.sso.v1.ClientSpec spec) {
        client.setName(spec.getDisplayedName() != null ? spec.getDisplayedName() : spec.getClientId());
        client.setDescription(spec.getDisplayedDescription());
        client.setEnabled(spec.isEnabled());
        
        // Настройка типа клиента из CRD
        if ("CONFIDENTIAL".equals(spec.getCapabilityType())) {
            client.setPublicClient(false);
            client.setProtocol(spec.getProtocol() != null ? spec.getProtocol() : "openid-connect");
            client.setClientAuthenticatorType(spec.getClientAuthenticatorType() != null ? spec.getClientAuthenticatorType() : "client-secret");
            client.setStandardFlowEnabled(spec.getStandardFlowEnabled() != null ? spec.getStandardFlowEnabled() : true);
            client.setServiceAccountsEnabled(spec.getServiceAccountsEnabled() != null ? spec.getServiceAccountsEnabled() : false);
            client.setDirectAccessGrantsEnabled(spec.getDirectAccessGrantsEnabled() != null ? spec.getDirectAccessGrantsEnabled() : false);
            client.setImplicitFlowEnabled(spec.getImplicitFlowEnabled() != null ? spec.getImplicitFlowEnabled() : false);
            client.setFullScopeAllowed(spec.getFullScopeAllowed() != null ? spec.getFullScopeAllowed() : true);
        } else {
            client.setPublicClient(true);
            client.setProtocol(spec.getProtocol() != null ? spec.getProtocol() : "openid-connect");
            client.setStandardFlowEnabled(spec.getStandardFlowEnabled() != null ? spec.getStandardFlowEnabled() : true);
            client.setServiceAccountsEnabled(spec.getServiceAccountsEnabled() != null ? spec.getServiceAccountsEnabled() : false);
            client.setDirectAccessGrantsEnabled(spec.getDirectAccessGrantsEnabled() != null ? spec.getDirectAccessGrantsEnabled() : false);
            client.setImplicitFlowEnabled(spec.getImplicitFlowEnabled() != null ? spec.getImplicitFlowEnabled() : false);
            client.setFullScopeAllowed(spec.getFullScopeAllowed() != null ? spec.getFullScopeAllowed() : true);
        }
        
        if (spec.getRedirectUris() != null) {
            java.util.Set<String> redirectUris = new java.util.HashSet<>();
            for (String uri : spec.getRedirectUris()) {
                redirectUris.add(uri);
            }
            client.setRedirectUris(redirectUris);
        }
        
        if (spec.getWebOrigins() != null) {
            java.util.Set<String> webOrigins = new java.util.HashSet<>();
            for (String origin : spec.getWebOrigins()) {
                webOrigins.add(origin);
            }
            client.setWebOrigins(webOrigins);
        }
        
        // Применяем attributes
        if (spec.getAttributes() != null) {
            for (String attribute : spec.getAttributes()) {
                client.setAttribute(attribute, "true");
                log.infof("Applied attribute: %s to client: %s", attribute, client.getClientId());
            }
        }
        
        // Создаем роли для клиента
        if (spec.getRoles() != null) {
            for (kz.kacd.sso.v1.RoleSpec roleSpec : spec.getRoles()) {
                createClientRole(client, roleSpec);
            }
        }
    }
    
    private void createClientRole(org.keycloak.models.ClientModel client, kz.kacd.sso.v1.RoleSpec roleSpec) {
        try {
            // Проверяем, существует ли уже роль
            org.keycloak.models.RoleModel existingRole = client.getRole(roleSpec.getName());
            if (existingRole != null) {
                log.infof("Role already exists: %s for client: %s", roleSpec.getName(), client.getClientId());
                return;
            }
            
            // Создаем новую роль
            org.keycloak.models.RoleModel role = client.addRole(roleSpec.getName());
            role.setDescription(roleSpec.getRoleDescription());
            
            log.infof("Created role: %s for client: %s", roleSpec.getName(), client.getClientId());
            
        } catch (Exception e) {
            log.errorf(e, "Error creating role: %s for client: %s", roleSpec.getName(), client.getClientId());
        }
    }
    
    private void applyClientAttributesAndRoles(org.keycloak.models.ClientModel client, String clientName) {
        // Применяем стандартные атрибуты
        String[] standardAttributes = {
            "FIRST_NAME", "LAST_NAME", "MIDDLE_NAME", "GROUPS", "LOCALE", "DIVISION"
        };
        
        for (String attribute : standardAttributes) {
            client.setAttribute(attribute, "true");
            log.infof("Applied attribute: %s to client: %s", attribute, clientName);
        }
        
        // Создаем стандартные роли
        createClientRole(client, "user", "Default user role for " + clientName);
        createClientRole(client, "admin", "Admin role for " + clientName);
        
        log.infof("Universal client configuration applied for: %s", clientName);
    }
    
    private void createClientRole(org.keycloak.models.ClientModel client, String roleName, String roleDescription) {
        try {
            // Проверяем, существует ли уже роль
            org.keycloak.models.RoleModel existingRole = client.getRole(roleName);
            if (existingRole != null) {
                log.infof("Role already exists: %s for client: %s", roleName, client.getClientId());
                return;
            }
            
            // Создаем новую роль
            org.keycloak.models.RoleModel role = client.addRole(roleName);
            role.setDescription(roleDescription);
            
            log.infof("Created role: %s for client: %s", roleName, client.getClientId());
            
        } catch (Exception e) {
            log.errorf(e, "Error creating role: %s for client: %s", roleName, client.getClientId());
        }
    }
}

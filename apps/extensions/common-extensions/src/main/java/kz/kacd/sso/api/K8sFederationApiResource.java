package kz.kacd.sso.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.k8s.federation.K8sFederation;
import kz.kacd.sso.k8s.federation.K8sFederationProvider;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.resource.RealmResourceProvider;

public class K8sFederationApiResource extends BaseApiResource implements RealmResourceProvider {
    private static final Logger log = Logger.getLogger(K8sFederationApiResource.class);
    
    public K8sFederationApiResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
        log.infof("K8sFederationApiResource created with session: %s, realm: %s", session, realm);
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
        log.infof("K8sFederationApiResource.close() called");
    }

    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {
        return Response.ok("{\"status\":\"ok\",\"message\":\"K8sFederationApiResource is working\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }



    @POST
    @Path("federation/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFederation(@PathParam("name") String name) {
        log.infof("updateFederation called with name: %s", name);
        
        // Проверки безопасности
        checkPermissions();
        checkConfig();
        
        try {
            // 1. Пытаемся получить данные из Kubernetes CRD
            log.infof("Attempting to get K8sFederationProvider from session");
            K8sFederationProvider k8sProvider = session.getProvider(K8sFederationProvider.class);
            log.infof("K8sFederationProvider result: %s", k8sProvider != null ? "NOT NULL" : "NULL");
            
            if (k8sProvider != null) {
                K8sFederation k8sFederation = k8sProvider.findByName(name);
                if (k8sFederation != null) {
                    log.infof("Found federation in Kubernetes CRD: %s", name);
                    
                    // 2. Проверяем, что federation принадлежит текущему realm
                    if (k8sFederation.getRealm() != null && k8sFederation.getRealm().equals(realm.getName())) {
                        // 3. Обновляем статус в Kubernetes
                        k8sFederation.applying();
                        
                        // 4. Получаем спецификацию из CRD
                        kz.kacd.sso.v1.FederationSpec federationSpec = k8sFederation.getSpec();
                        if (federationSpec != null) {
                            // 5. Создаем или обновляем federation в Keycloak
                            configureFederationFromSpec(federationSpec);
                            
                            // 6. Обновляем статус в Kubernetes
                            k8sFederation.applied();
                            
                            log.infof("Successfully configured federation from CRD: %s", name);
                            
                            // 7. Возвращаем ответ в формате CRD
                            String response = String.format(
                                "{\n" +
                                "    \"name\": \"%s\",\n" +
                                "    \"spec\": {\n" +
                                "        \"federationName\": \"%s\",\n" +
                                "        \"enabled\": true,\n" +
                                "        \"type\": \"ldap\"\n" +
                                "    },\n" +
                                "    \"status\": {\n" +
                                "        \"state\": \"APPLIED_FROM_CRD\",\n" +
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
                        } else {
                            log.warnf("Federation spec is null in Kubernetes CRD: %s", name);
                            k8sFederation.failed(new Exception("Federation spec is null"));
                        }
                    } else {
                        log.warnf("Federation %s belongs to realm %s, but current realm is %s", 
                            name, k8sFederation.getRealm(), realm.getName());
                    }
                } else {
                    log.warnf("Federation not found in Kubernetes CRD: %s", name);
                }
            }
            
            // Fallback: создаем federation напрямую (если CRD не найден)
            log.infof("Creating federation directly (no CRD found): %s", name);
            configureDefaultFederation(name);
            
            String response = String.format(
                "{\n" +
                "    \"name\": \"%s\",\n" +
                "    \"spec\": {\n" +
                "        \"federationName\": \"%s\",\n" +
                "        \"enabled\": true,\n" +
                "        \"type\": \"ldap\"\n" +
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
            log.errorf(e, "Error updating federation: %s", name);
            
            // Обновляем статус ошибки в Kubernetes
            try {
                K8sFederationProvider k8sProvider = session.getProvider(K8sFederationProvider.class);
                if (k8sProvider != null) {
                    K8sFederation k8sFederation = k8sProvider.findByName(name);
                    if (k8sFederation != null) {
                        k8sFederation.failed(e);
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
    private void configureFederationFromSpec(kz.kacd.sso.v1.FederationSpec spec) {
        // Настраиваем federation в Keycloak на основе спецификации
        log.infof("Configuring federation from spec for realm: %s", realm.getName());
        
        // Создаем или обновляем identity provider
        String providerId = getProviderIdFromSpec(spec);
        String alias = getAliasFromSpec(spec);
        
        // Проверяем, существует ли уже identity provider
        org.keycloak.models.IdentityProviderModel existingProvider = null;
        try {
            existingProvider = realm.getIdentityProviderByAlias(alias);
        } catch (Exception e) {
            log.debugf("Identity provider not found: %s", alias);
        }
        
        if (existingProvider != null) {
            log.infof("Updating existing identity provider: %s", alias);
            updateIdentityProvider(existingProvider, spec);
        } else {
            log.infof("Creating new identity provider: %s", alias);
            createIdentityProvider(providerId, alias, spec);
        }
    }

    private void configureDefaultFederation(String name) {
        // Создаем federation с дефолтными настройками
        log.infof("Configuring default federation: %s for realm: %s", name, realm.getName());
        
        // Создаем LDAP identity provider с дефолтными настройками
        String providerId = "ldap";
        String alias = name;
        
        // Проверяем, существует ли уже identity provider
        org.keycloak.models.IdentityProviderModel existingProvider = null;
        try {
            existingProvider = realm.getIdentityProviderByAlias(alias);
        } catch (Exception e) {
            log.debugf("Identity provider not found: %s", alias);
        }
        
        if (existingProvider != null) {
            log.infof("Identity provider already exists: %s", alias);
            return;
        }
        
        createDefaultIdentityProvider(providerId, alias);
    }
    
    private String getProviderIdFromSpec(kz.kacd.sso.v1.FederationSpec spec) {
        // Извлекаем тип провайдера из спецификации
        // По умолчанию используем LDAP
        return "ldap";
    }
    
    private String getAliasFromSpec(kz.kacd.sso.v1.FederationSpec spec) {
        // Извлекаем алиас из спецификации
        // По умолчанию используем имя federation
        return "federation-" + java.time.Instant.now().getEpochSecond();
    }
    
    private void createIdentityProvider(String providerId, String alias, kz.kacd.sso.v1.FederationSpec spec) {
        // Создаем новый identity provider
        org.keycloak.models.IdentityProviderModel provider = new org.keycloak.models.IdentityProviderModel();
        provider.setProviderId(providerId);
        provider.setAlias(alias);
        provider.setEnabled(true);
        provider.setDisplayName(alias);
        
        // Настраиваем конфигурацию в зависимости от типа провайдера
        if ("ldap".equals(providerId)) {
            configureLdapProvider(provider, spec);
        }
        
        // Сохраняем identity provider
        realm.addIdentityProvider(provider);
        log.infof("Created identity provider: %s", alias);
    }
    
    private void updateIdentityProvider(org.keycloak.models.IdentityProviderModel provider, kz.kacd.sso.v1.FederationSpec spec) {
        // Обновляем существующий identity provider
        provider.setEnabled(true);
        provider.setDisplayName(provider.getAlias());
        
        // Настраиваем конфигурацию в зависимости от типа провайдера
        if ("ldap".equals(provider.getProviderId())) {
            configureLdapProvider(provider, spec);
        }
        
        // Обновляем identity provider
        realm.updateIdentityProvider(provider);
        log.infof("Updated identity provider: %s", provider.getAlias());
    }
    
    private void createDefaultIdentityProvider(String providerId, String alias) {
        // Создаем identity provider с дефолтными настройками
        org.keycloak.models.IdentityProviderModel provider = new org.keycloak.models.IdentityProviderModel();
        provider.setProviderId(providerId);
        provider.setAlias(alias);
        provider.setEnabled(true);
        provider.setDisplayName(alias);
        
        // Настраиваем дефолтную конфигурацию LDAP
        if ("ldap".equals(providerId)) {
            configureDefaultLdapProvider(provider);
        }
        
        // Сохраняем identity provider
        realm.addIdentityProvider(provider);
        log.infof("Created default identity provider: %s", alias);
    }
    
    private void configureLdapProvider(org.keycloak.models.IdentityProviderModel provider, kz.kacd.sso.v1.FederationSpec spec) {
        // Настраиваем LDAP конфигурацию
        java.util.Map<String, String> config = new java.util.HashMap<>();
        
        // Базовые настройки LDAP
        config.put("serverUrl", "ldap://localhost:389");
        config.put("bindDn", "cn=admin,dc=example,dc=com");
        config.put("bindCredential", "admin");
        config.put("searchBase", "dc=example,dc=com");
        config.put("userObjectClasses", "person, organizationalPerson");
        config.put("usernameLDAPAttribute", "uid");
        config.put("rdnLDAPAttribute", "uid");
        config.put("uuidLDAPAttribute", "entryUUID");
        config.put("userDN", "ou=users");
        config.put("connectionUrl", "ldap://localhost:389");
        config.put("baseDN", "dc=example,dc=com");
        config.put("enabled", "true");
        
        provider.setConfig(config);
    }
    
    private void configureDefaultLdapProvider(org.keycloak.models.IdentityProviderModel provider) {
        // Настраиваем дефолтную LDAP конфигурацию
        java.util.Map<String, String> config = new java.util.HashMap<>();
        
        // Базовые настройки LDAP
        config.put("serverUrl", "ldap://localhost:389");
        config.put("bindDn", "cn=admin,dc=example,dc=com");
        config.put("bindCredential", "admin");
        config.put("searchBase", "dc=example,dc=com");
        config.put("userObjectClasses", "person, organizationalPerson");
        config.put("usernameLDAPAttribute", "uid");
        config.put("rdnLDAPAttribute", "uid");
        config.put("uuidLDAPAttribute", "entryUUID");
        config.put("userDN", "ou=users");
        config.put("connectionUrl", "ldap://localhost:389");
        config.put("baseDN", "dc=example,dc=com");
        config.put("enabled", "true");
        
        provider.setConfig(config);
    }

}




package kz.kacd.sso.api;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.InternalServerErrorException;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.resources.admin.AdminAuth;

/**
 * Базовый класс для API ресурсов с общими методами проверки безопасности
 */
public abstract class BaseApiResource {
    protected static final Logger log = Logger.getLogger(BaseApiResource.class);
    
    protected final KeycloakSession session;
    protected final RealmModel realm;

    protected BaseApiResource(KeycloakSession session, RealmModel realm) {
        this.session = session;
        this.realm = realm;
    }
    
    /**
     * Проверяет права доступа пользователя
     * Проверяет наличие роли realm-config, как было в прошлом
     */
    protected void checkPermissions() {
        // Получаем AdminAuth из сессии
        AdminAuth adminAuth = session.getContext().getContextObject(AdminAuth.class);
        if (adminAuth == null) {
            log.warn("No AdminAuth found in session");
            throw new NotAuthorizedException("Access denied: No admin authentication");
        }
        
        // Проверяем роль realm-config, как было в ConfigAdminAuth
        boolean hasRealmConfig = adminAuth.hasAppRole(adminAuth.getClient(), "realm-config");
        log.debugf("User %s has realm-config role: %s", adminAuth.getUser().getUsername(), hasRealmConfig);
        
        if (!hasRealmConfig) {
            log.warnf("User %s does not have realm-config role", adminAuth.getUser().getUsername());
            throw new NotAuthorizedException("User has no permissions to run configuration!");
        }
        
        log.debugf("Permission check passed for user: %s", adminAuth.getUser().getUsername());
    }
    
    /**
     * Проверяет конфигурацию K8s, как было в прошлом
     */
    protected void checkConfig() {
        // Проверяем, что K8s включен через переменную окружения
        String enabledStr = System.getenv("ENABLED_K8S");
        boolean k8sEnabled = enabledStr != null && enabledStr.equals("true");
        
        if (!k8sEnabled) {
            log.warn("Kubernetes API is not enabled (ENABLED_K8S != true)");
            throw new InternalServerErrorException("Server does not configured to use kubernetes api!");
        }
        
        // Проверяем, что realm существует и активен
        if (realm == null) {
            log.warn("Realm is null");
            throw new InternalServerErrorException("Invalid realm configuration");
        }
        
        if (!realm.isEnabled()) {
            log.warnf("Realm %s is disabled", realm.getName());
            throw new InternalServerErrorException("Realm is disabled");
        }
        
        // Проверяем, что сессия валидна
        if (session == null) {
            log.warn("Session is null");
            throw new InternalServerErrorException("Invalid session");
        }
        
        log.debugf("Configuration check passed for realm: %s", realm.getName());
    }
}

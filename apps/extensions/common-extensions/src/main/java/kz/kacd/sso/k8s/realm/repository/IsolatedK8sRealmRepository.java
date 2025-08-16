package kz.kacd.sso.k8s.realm.repository;

import kz.kacd.sso.k8s.K8sConfig;
import org.jboss.logging.Logger;

import java.lang.reflect.Method;

public class IsolatedK8sRealmRepository {
    private static final Logger log = Logger.getLogger(IsolatedK8sRealmRepository.class);

    private final Object facade;

    public IsolatedK8sRealmRepository(Object facade) {
        this.facade = facade;
    }

    public void updateStatus(String name, Object status) {
        try {
            log.debugf("Updating status in kubernetes for %s ...", name);
            Object existing = find(name);
            if (existing == null) {
                throw new IllegalStateException("Cannot update status for realm " + name + " because it does not exist!");
            }

            // Use reflection to call facade methods
            Method updateStatusMethod = facade.getClass().getMethod("updateResourceStatus", Object.class, Object.class);
            updateStatusMethod.invoke(facade, existing, status);
            
        } catch (Exception e) {
            log.error("Failed to update status for realm " + name, e);
            throw new RuntimeException("Failed to update status", e);
        }
    }

    public Object find(String name) {
        try {
            log.debugf("Finding realm in kubernetes %s ...", name);
            
            Method findResourceMethod = facade.getClass().getMethod("findResource", String.class, String.class, String.class);
            Object result = findResourceMethod.invoke(facade, "Realm", name, K8sConfig.NAMESPACE);
            
            return result;
            
        } catch (Exception e) {
            log.error("Failed to find realm " + name, e);
            return null;
        }
    }
}

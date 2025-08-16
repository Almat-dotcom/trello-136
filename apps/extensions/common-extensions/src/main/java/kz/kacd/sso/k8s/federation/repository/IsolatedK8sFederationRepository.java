package kz.kacd.sso.k8s.federation.repository;

import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.realm.K8sRealm;
import org.jboss.logging.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

public class IsolatedK8sFederationRepository {
    private static final Logger log = Logger.getLogger(IsolatedK8sFederationRepository.class);

    private final Object facade;

    public IsolatedK8sFederationRepository(Object facade) {
        this.facade = facade;
    }

    public void updateStatus(String name, Object status) {
        try {
            log.debugf("Updating status in kubernetes for %s ...", name);
            Object existing = find(name);
            if (existing == null) {
                throw new IllegalStateException("Cannot update status for federation " + name + " because it does not exist!");
            }

            // Use reflection to call facade methods
            Method updateStatusMethod = facade.getClass().getMethod("updateResourceStatus", Object.class, Object.class);
            updateStatusMethod.invoke(facade, existing, status);
            
        } catch (Exception e) {
            log.error("Failed to update status for federation " + name, e);
            throw new RuntimeException("Failed to update status", e);
        }
    }

    public Object find(String name) {
        try {
            log.debugf("Finding federation in kubernetes %s ...", name);
            
            Method findResourceMethod = facade.getClass().getMethod("findResource", String.class, String.class, String.class);
            Object result = findResourceMethod.invoke(facade, "Federation", name, K8sConfig.NAMESPACE);
            
            return result;
            
        } catch (Exception e) {
            log.error("Failed to find federation " + name, e);
            return null;
        }
    }

    public List<Object> findByRealm(String realmName) {
        try {
            log.debugf("Finding federations by realm %s ...", realmName);
            
            Method findResourcesByLabelMethod = facade.getClass().getMethod("findResourcesByLabel", String.class, String.class, String.class, String.class);
            List<Object> items = (List<Object>) findResourcesByLabelMethod.invoke(facade, "Federation", K8sConfig.NAMESPACE, K8sRealm.REALM_LABEL, realmName);
            
            return items;
                
        } catch (Exception e) {
            log.error("Failed to find federations by realm " + realmName, e);
            return new ArrayList<>();
        }
    }
}

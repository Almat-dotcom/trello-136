package kz.kacd.sso.k8s.client.repository;

import kz.kacd.sso.k8s.K8sConfig;
import kz.kacd.sso.k8s.realm.K8sRealm;
import org.jboss.logging.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class IsolatedK8sClientRepository {
    private static final Logger log = Logger.getLogger(IsolatedK8sClientRepository.class);

    private final Object facade;

    public IsolatedK8sClientRepository(Object facade) {
        this.facade = facade;
    }

    public void updateStatus(String name, String state, String message, String generation, String lastApplication) {
        try {
            log.debugf("Updating status in kubernetes for %s to %s ...", name, state);
            Object existing = find(name);
            if (existing == null) {
                throw new IllegalStateException("Cannot update status for client " + name + " because it does not exist!");
            }

            // Create status object using reflection
            Object status = createStatusObject(state, message, generation, lastApplication);
            
            // Use reflection to call facade methods
            Method updateStatusMethod = facade.getClass().getMethod("updateResourceStatus", Object.class, Object.class);
            updateStatusMethod.invoke(facade, existing, status);
            
        } catch (Exception e) {
            log.error("Failed to update status for client " + name, e);
            throw new RuntimeException("Failed to update status", e);
        }
    }

    private Object createStatusObject(String state, String message, String generation, String lastApplication) {
        try {
            // Create ClientStatus object using reflection
            Class<?> statusClass = Class.forName("kz.kacd.sso.k8s.bundle.v1.ClientStatus");
            Object status = statusClass.getDeclaredConstructor().newInstance();
            
            // Set state enum
            Class<?> stateEnum = Class.forName("kz.kacd.sso.k8s.bundle.v1.ClientStatus$State");
            Object stateValue = Enum.valueOf((Class<Enum>) stateEnum, state);
            statusClass.getMethod("setState", stateEnum).invoke(status, stateValue);
            
            // Set other fields
            statusClass.getMethod("setMessage", String.class).invoke(status, message);
            statusClass.getMethod("setGeneration", String.class).invoke(status, generation);
            statusClass.getMethod("setLastApplication", String.class).invoke(status, lastApplication);
            
            return status;
        } catch (Exception e) {
            log.error("Failed to create status object", e);
            throw new RuntimeException("Failed to create status object", e);
        }
    }

    public Object find(String name) {
        try {
            log.debugf("Finding client in kubernetes %s ...", name);
            
            Method findResourceMethod = facade.getClass().getMethod("findResource", String.class, String.class, String.class);
            Object result = findResourceMethod.invoke(facade, "Client", name, K8sConfig.NAMESPACE);
            
            return result;
            
        } catch (Exception e) {
            log.error("Failed to find client " + name, e);
            return null;
        }
    }

    public List<Object> findByRealm(String realmName) {
        try {
            log.debugf("Finding clients by realm %s ...", realmName);
            
            Method findResourcesByLabelMethod = facade.getClass().getMethod("findResourcesByLabel", String.class, String.class, String.class, String.class);
            List<Object> items = (List<Object>) findResourcesByLabelMethod.invoke(facade, "Client", K8sConfig.NAMESPACE, K8sRealm.REALM_LABEL, realmName);
            
            return items;
                
        } catch (Exception e) {
            log.error("Failed to find clients by realm " + realmName, e);
            return new ArrayList<>();
        }
    }
}

package kz.kacd.sso.k8s.bundle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Facade for working with isolated Fabric8 Kubernetes Client.
 * This class provides a bridge between the main application and the isolated k8s-bundle JAR.
 */
public class K8sBundleFacade {
    
    private final Object kubernetesClient;
    private final ClassLoader bundleClassLoader;
    
    public K8sBundleFacade(ClassLoader bundleClassLoader) throws Exception {
        this.bundleClassLoader = bundleClassLoader;
        
        // Create KubernetesClient using reflection
        Class<?> clientBuilderClass = bundleClassLoader.loadClass("kz.kacd.sso.k8s.bundle.fabric8.kubernetes.client.KubernetesClientBuilder");
        Constructor<?> constructor = clientBuilderClass.getDeclaredConstructor();
        Object clientBuilder = constructor.newInstance();
        
        Method buildMethod = clientBuilderClass.getMethod("build");
        this.kubernetesClient = buildMethod.invoke(clientBuilder);
    }
    
    /**
     * Find a resource by name
     */
    public Object findResource(String resourceType, String name, String namespace) throws Exception {
        Class<?> resourceClass = bundleClassLoader.loadClass("kz.kacd.sso.k8s.bundle.fabric8.kubernetes.client.dsl.MixedOperation");
        
        Method resourcesMethod = kubernetesClient.getClass().getMethod("resources", Class.class, Class.class);
        Object resourceOperation = resourcesMethod.invoke(kubernetesClient, 
            bundleClassLoader.loadClass("kz.kacd.sso.v1." + resourceType),
            bundleClassLoader.loadClass("kz.kacd.sso.v1." + resourceType + "List"));
        
        Method inNamespaceMethod = resourceOperation.getClass().getMethod("inNamespace", String.class);
        Object namespacedOperation = inNamespaceMethod.invoke(resourceOperation, namespace);
        
        Method withNameMethod = namespacedOperation.getClass().getMethod("withName", String.class);
        Object namedOperation = withNameMethod.invoke(namespacedOperation, name);
        
        Method getMethod = namedOperation.getClass().getMethod("get");
        return getMethod.invoke(namedOperation);
    }
    
    /**
     * Find resources by label
     */
    public List<Object> findResourcesByLabel(String resourceType, String namespace, String labelKey, String labelValue) throws Exception {
        Class<?> resourceClass = bundleClassLoader.loadClass("kz.kacd.sso.k8s.bundle.fabric8.kubernetes.client.dsl.MixedOperation");
        
        Method resourcesMethod = kubernetesClient.getClass().getMethod("resources", Class.class, Class.class);
        Object resourceOperation = resourcesMethod.invoke(kubernetesClient, 
            bundleClassLoader.loadClass("kz.kacd.sso.v1." + resourceType),
            bundleClassLoader.loadClass("kz.kacd.sso.v1." + resourceType + "List"));
        
        Method inNamespaceMethod = resourceOperation.getClass().getMethod("inNamespace", String.class);
        Object namespacedOperation = inNamespaceMethod.invoke(resourceOperation, namespace);
        
        Method withLabelMethod = namespacedOperation.getClass().getMethod("withLabel", String.class, String.class);
        Object labeledOperation = withLabelMethod.invoke(namespacedOperation, labelKey, labelValue);
        
        Method listMethod = labeledOperation.getClass().getMethod("list");
        Object listResult = listMethod.invoke(labeledOperation);
        
        Method getItemsMethod = listResult.getClass().getMethod("getItems");
        return (List<Object>) getItemsMethod.invoke(listResult);
    }
    
    /**
     * Update resource status
     */
    public void updateResourceStatus(Object resource, Object status) throws Exception {
        // Set status on resource
        Method setStatusMethod = resource.getClass().getMethod("setStatus", status.getClass());
        setStatusMethod.invoke(resource, status);
        
        // Patch status
        Method resourceMethod = kubernetesClient.getClass().getMethod("resource", Object.class);
        Object resourceOperation = resourceMethod.invoke(kubernetesClient, resource);
        
        Method patchStatusMethod = resourceOperation.getClass().getMethod("patchStatus");
        patchStatusMethod.invoke(resourceOperation);
    }
    
    /**
     * Get secret by name
     */
    public Object getSecret(String name) throws Exception {
        Method secretsMethod = kubernetesClient.getClass().getMethod("secrets");
        Object secretsOperation = secretsMethod.invoke(kubernetesClient);
        
        Method withNameMethod = secretsOperation.getClass().getMethod("withName", String.class);
        Object namedOperation = withNameMethod.invoke(secretsOperation, name);
        
        Method getMethod = namedOperation.getClass().getMethod("get");
        return getMethod.invoke(namedOperation);
    }
    
    /**
     * Create or replace secret
     */
    public Object createOrReplaceSecret(Object secret) throws Exception {
        Method resourceMethod = kubernetesClient.getClass().getMethod("resource", Object.class);
        Object resourceOperation = resourceMethod.invoke(kubernetesClient, secret);
        
        Method createOrReplaceMethod = resourceOperation.getClass().getMethod("createOrReplace");
        return createOrReplaceMethod.invoke(resourceOperation);
    }
    
    /**
     * Close the client
     */
    public void close() throws Exception {
        if (kubernetesClient != null) {
            Method closeMethod = kubernetesClient.getClass().getMethod("close");
            closeMethod.invoke(kubernetesClient);
        }
    }
    
    /**
     * Get the underlying KubernetesClient (for advanced usage)
     */
    public Object getKubernetesClient() {
        return kubernetesClient;
    }
}


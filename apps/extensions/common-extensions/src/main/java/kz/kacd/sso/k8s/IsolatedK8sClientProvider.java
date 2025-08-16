package kz.kacd.sso.k8s;

import org.jboss.logging.Logger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicReference;

/**
 * K8sClientProvider that uses an isolated k8s-bundle JAR to avoid conflicts with Keycloak 23.
 */
public class IsolatedK8sClientProvider implements K8sClientProvider {
    private static final Logger log = Logger.getLogger(IsolatedK8sClientProvider.class);
    
    private final AtomicReference<Object> facadeRef = new AtomicReference<>();
    private final AtomicReference<URLClassLoader> classLoaderRef = new AtomicReference<>();
    private volatile boolean initialized = false;
    
    public IsolatedK8sClientProvider() {
        // Don't initialize immediately - use lazy loading
        log.debug("IsolatedK8sClientProvider created, will initialize on first use");
    }
    
    private synchronized void initializeBundle() {
        if (initialized) {
            return;
        }
        
        try {
            // Find the k8s-bundle JAR
            String bundlePath = findK8sBundleJar();
            if (bundlePath == null) {
                log.warn("Could not find k8s-bundle.jar, will retry later");
                return; // Don't throw exception, just return and retry later
            }
            
            log.debugf("Loading k8s-bundle from: %s", bundlePath);
            
            // Create isolated class loader
            URL bundleUrl = new File(bundlePath).toURI().toURL();
            URLClassLoader bundleClassLoader = new URLClassLoader(
                new URL[]{bundleUrl}, 
                null // Parent class loader is null to isolate completely
            );
            
            // Create facade
            Class<?> facadeClass = bundleClassLoader.loadClass("kz.kacd.sso.k8s.bundle.K8sBundleFacade");
            Object facade = facadeClass.getConstructor(ClassLoader.class).newInstance(bundleClassLoader);
            
            facadeRef.set(facade);
            classLoaderRef.set(bundleClassLoader);
            initialized = true;
            
            log.debug("K8s bundle initialized successfully");
            
        } catch (Exception e) {
            log.warn("Failed to initialize k8s bundle, will retry later: " + e.getMessage());
            // Don't throw exception, just log warning and retry later
        }
    }
    
    private String findK8sBundleJar() {
        // Look in common locations for the k8s-bundle.jar
        String[] possiblePaths = {
            "k8s-bundle.jar",
            "target/k8s-bundle.jar",
            "../k8s-bundle/target/k8s-bundle.jar",
            "lib/k8s-bundle.jar",
            "standalone/lib/k8s-bundle.jar",
            "modules/k8s-bundle.jar"
        };
        
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                log.debugf("Found k8s-bundle.jar at: %s", file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        }
        
        // Also check classpath
        String classpath = System.getProperty("java.class.path");
        if (classpath != null) {
            for (String path : classpath.split(File.pathSeparator)) {
                if (path.contains("k8s-bundle") && path.endsWith(".jar")) {
                    log.debugf("Found k8s-bundle.jar in classpath: %s", path);
                    return path;
                }
            }
        }
        
        log.debug("k8s-bundle.jar not found in any of the expected locations");
        return null;
    }
    
    @Override
    public Object getClient() {
        if (!initialized) {
            initializeBundle();
        }
        
        Object facade = facadeRef.get();
        if (facade == null) {
            log.warn("K8s bundle not initialized yet, returning null");
            return null;
        }
        
        return facade;
    }
    
    @Override
    public void close() {
        try {
            Object facade = facadeRef.get();
            if (facade != null) {
                facade.getClass().getMethod("close").invoke(facade);
            }
            
            URLClassLoader classLoader = classLoaderRef.get();
            if (classLoader != null) {
                classLoader.close();
            }
            
            initialized = false;
        } catch (Exception e) {
            log.warn("Error closing k8s bundle", e);
        }
    }
}

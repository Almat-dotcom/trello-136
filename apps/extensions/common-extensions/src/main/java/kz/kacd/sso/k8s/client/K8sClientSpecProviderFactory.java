package kz.kacd.sso.k8s.client;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderFactory;

@AutoService(ProviderFactory.class)
public class K8sClientSpecProviderFactory implements ProviderFactory<K8sClientSpecProvider> {
    private static final Logger log = Logger.getLogger(K8sClientSpecProviderFactory.class);
    
    @Override
    public K8sClientSpecProvider create(KeycloakSession session) {
        log.infof("K8sClientSpecProviderFactory.create() called with session: %s", session);
        try {
            // Проверяем, нужно ли использовать YAML режим
            String yamlMode = System.getenv("YAML_MODE");
            log.infof("YAML_MODE environment variable: %s", yamlMode);
            
            if ("true".equals(yamlMode)) {
                // Используем YAML провайдер для чтения файлов напрямую
                String yamlDirectory = System.getenv("YAML_CLIENTS_DIRECTORY");
                if (yamlDirectory == null) {
                    yamlDirectory = "/opt/keycloak/clients";
                }
                log.infof("Using YAML mode with directory: %s", yamlDirectory);
                
                K8sClientSpecProvider provider = new YamlClientSpecProviderImpl(yamlDirectory);
                log.infof("YamlClientSpecProvider created successfully");
                return provider;
            } else {
                // Используем реальный Kubernetes клиент
                log.infof("Using real Kubernetes client");
                io.fabric8.kubernetes.client.KubernetesClient k8sClient = new io.fabric8.kubernetes.client.DefaultKubernetesClient();
                K8sClientSpecProvider provider = new K8sClientSpecProviderImpl(k8sClient);
                log.infof("K8sClientSpecProvider created successfully");
                return provider;
            }
        } catch (Exception e) {
            log.errorf(e, "Error creating K8sClientSpecProvider");
            throw new RuntimeException("Failed to create K8sClientSpecProvider", e);
        }
    }

    @Override
    public void init(Config.Scope config) {
        log.infof("K8sClientSpecProviderFactory.init() called");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        log.infof("K8sClientSpecProviderFactory.postInit() called");
        try {
            // Проверяем, что фабрика может создать провайдер
            KeycloakSession session = factory.create();
            K8sClientSpecProvider provider = create(session);
            log.infof("Successfully created K8sClientSpecProvider in postInit: %s", provider);
            session.close();
        } catch (Exception e) {
            log.errorf(e, "Error creating K8sClientSpecProvider in postInit");
        }
    }

    @Override
    public void close() {
        log.infof("K8sClientSpecProviderFactory.close() called");
    }

    @Override
    public String getId() {
        return "k8s-client-spec";
    }
}


package kz.kacd.sso.k8s.client;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import kz.kacd.sso.v1.ClientSpec;
import kz.kacd.sso.v1.ClientStatus;
import kz.kacd.sso.v1.RoleSpec;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class K8sClientSpecProviderImpl implements K8sClientSpecProvider {
    private static final Logger log = Logger.getLogger(K8sClientSpecProviderImpl.class);
    
    private final KubernetesClient k8sClient;
    private final CustomResourceDefinitionContext crdContext;

    public K8sClientSpecProviderImpl(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
        
        // Создаем контекст для CRD clients.sso.kacd.kz
        this.crdContext = new CustomResourceDefinitionContext.Builder()
                .withGroup("sso.kacd.kz")
                .withVersion("v1")
                .withScope("Namespaced")
                .withPlural("clients")
                .build();
        
        log.infof("K8sClientSpecProviderImpl initialized successfully");
    }

    @Override
    public K8sClient findByName(String name) {
        try {
            log.infof("Looking for K8s client with name: %s", name);
            
            if (k8sClient == null) {
                log.warnf("K8sClient is null, cannot find client: %s", name);
                return null;
            }
            
            // Ищем клиент в Kubernetes CRD
            String namespace = getCurrentNamespace();
            log.infof("Searching for client %s in namespace %s", name, namespace);
            
            try {
                GenericKubernetesResource resource = k8sClient.genericKubernetesResources(crdContext)
                        .inNamespace(namespace)
                        .withName(name)
                        .get();
                
                if (resource != null) {
                    log.infof("Found client %s in Kubernetes CRD", name);
                    return new K8sClientImpl(resource, k8sClient, crdContext);
                } else {
                    log.warnf("Client %s not found in Kubernetes CRD", name);
                    return null;
                }
            } catch (Exception e) {
                log.errorf(e, "Error searching for client %s in Kubernetes", name);
                return null;
            }
            
        } catch (Exception e) {
            log.errorf(e, "Error finding K8s client: %s", name);
            return null;
        }
    }

    private String getCurrentNamespace() {
        try {
            // Try to get namespace from service account
            String namespace = k8sClient.getNamespace();
            if (namespace != null && !namespace.isEmpty()) {
                return namespace;
            }
            
            // Fallback to default namespace
            return "default";
        } catch (Exception e) {
            log.warnf("Could not determine namespace, using default: %s", e.getMessage());
            return "default";
        }
    }

    @Override
    public void close() {
        if (k8sClient != null) {
            k8sClient.close();
        }
        log.infof("K8sClientSpecProviderImpl closed");
    }
    
    private static class K8sClientImpl implements K8sClient {
        private final GenericKubernetesResource resource;
        private final KubernetesClient k8sClient;
        private final CustomResourceDefinitionContext crdContext;
        
        public K8sClientImpl(GenericKubernetesResource resource, KubernetesClient k8sClient, CustomResourceDefinitionContext crdContext) {
            this.resource = resource;
            this.k8sClient = k8sClient;
            this.crdContext = crdContext;
        }

        @Override
        public String getName() {
            return resource.getMetadata().getName();
        }

        @Override
        public String getRealm() {
            // Получаем realm из метки kz-kacd-realm-name
            Map<String, String> labels = resource.getMetadata().getLabels();
            if (labels != null && labels.containsKey("kz-kacd-realm-name")) {
                return labels.get("kz-kacd-realm-name");
            }
            return resource.getMetadata().getNamespace();
        }

        @Override
        public ClientSpec getSpec() {
            Map<String, Object> spec = (Map<String, Object>) resource.getAdditionalProperties().get("spec");
            if (spec == null) {
                log.warnf("Spec is null for client: %s", getName());
                return createDefaultClientSpec();
            }
            
            return new ClientSpec() {
                @Override
                public String getClientId() {
                    return (String) spec.get("clientId");
                }
                
                @Override
                public boolean isEnabled() {
                    Object enabled = spec.get("enabled");
                    return enabled == null || (Boolean) enabled;
                }
                
                @Override
                public String[] getRedirectUris() {
                    // Пытаемся получить из spec.access.validRedirectUris
                    Object access = spec.get("access");
                    if (access instanceof Map) {
                        Map<?, ?> accessMap = (Map<?, ?>) access;
                        Object redirectUris = accessMap.get("validRedirectUris");
                        if (redirectUris instanceof List) {
                            List<?> list = (List<?>) redirectUris;
                            return list.stream().map(Object::toString).toArray(String[]::new);
                        }
                    }
                    
                    // Fallback к прямому полю
                    Object redirectUris = spec.get("redirectUris");
                    if (redirectUris instanceof List) {
                        List<?> list = (List<?>) redirectUris;
                        return list.stream().map(Object::toString).toArray(String[]::new);
                    }
                    
                    return new String[]{"http://localhost:3000/*"};
                }
                
                @Override
                public String[] getWebOrigins() {
                    // Пытаемся получить из spec.access.webOrigins
                    Object access = spec.get("access");
                    if (access instanceof Map) {
                        Map<?, ?> accessMap = (Map<?, ?>) access;
                        Object webOrigins = accessMap.get("webOrigins");
                        if (webOrigins instanceof List) {
                            List<?> list = (List<?>) webOrigins;
                            return list.stream().map(Object::toString).toArray(String[]::new);
                        }
                    }
                    
                    // Fallback к прямому полю
                    Object webOrigins = spec.get("webOrigins");
                    if (webOrigins instanceof List) {
                        List<?> list = (List<?>) webOrigins;
                        return list.stream().map(Object::toString).toArray(String[]::new);
                    }
                    
                    return new String[]{"http://localhost:3000"};
                }
                
                @Override
                public String getDisplayedName() {
                    Object displayedName = spec.get("displayedName");
                    return displayedName != null ? displayedName.toString() : getName();
                }
                
                @Override
                public String getDisplayedDescription() {
                    Object displayedDescription = spec.get("displayedDescription");
                    return displayedDescription != null ? displayedDescription.toString() : "";
                }
                
                @Override
                public String getCapabilityType() {
                    Object capability = spec.get("capability");
                    if (capability instanceof Map) {
                        Map<?, ?> capabilityMap = (Map<?, ?>) capability;
                        Object type = capabilityMap.get("type");
                        return type != null ? type.toString() : "PUBLIC";
                    }
                    return "PUBLIC";
                }
                
                @Override
                public String getClientExistingSecret() {
                    Object capability = spec.get("capability");
                    if (capability instanceof Map) {
                        Map<?, ?> capabilityMap = (Map<?, ?>) capability;
                        Object secret = capabilityMap.get("clientExistingSecret");
                        return secret != null ? secret.toString() : null;
                    }
                    return null;
                }
                
                @Override
                public String getClientSecretKey() {
                    Object capability = spec.get("capability");
                    if (capability instanceof Map) {
                        Map<?, ?> capabilityMap = (Map<?, ?>) capability;
                        Object secretKey = capabilityMap.get("clientSecretKey");
                        return secretKey != null ? secretKey.toString() : null;
                    }
                    return null;
                }
                
                @Override
                public String getProtocol() {
                    Object protocol = spec.get("protocol");
                    return protocol != null ? protocol.toString() : "openid-connect";
                }
                
                @Override
                public String getClientAuthenticatorType() {
                    Object clientAuthenticatorType = spec.get("clientAuthenticatorType");
                    return clientAuthenticatorType != null ? clientAuthenticatorType.toString() : "client-secret";
                }
                
                @Override
                public Boolean getFullScopeAllowed() {
                    Object fullScopeAllowed = spec.get("fullScopeAllowed");
                    return fullScopeAllowed != null ? (Boolean) fullScopeAllowed : true;
                }
                
                @Override
                public Boolean getStandardFlowEnabled() {
                    Object standardFlowEnabled = spec.get("standardFlowEnabled");
                    return standardFlowEnabled != null ? (Boolean) standardFlowEnabled : true;
                }
                
                @Override
                public Boolean getServiceAccountsEnabled() {
                    Object serviceAccountsEnabled = spec.get("serviceAccountsEnabled");
                    return serviceAccountsEnabled != null ? (Boolean) serviceAccountsEnabled : false;
                }
                
                @Override
                public Boolean getDirectAccessGrantsEnabled() {
                    Object directAccessGrantsEnabled = spec.get("directAccessGrantsEnabled");
                    return directAccessGrantsEnabled != null ? (Boolean) directAccessGrantsEnabled : false;
                }
                
                @Override
                public Boolean getImplicitFlowEnabled() {
                    Object implicitFlowEnabled = spec.get("implicitFlowEnabled");
                    return implicitFlowEnabled != null ? (Boolean) implicitFlowEnabled : false;
                }
                
                @Override
                public String[] getAttributes() {
                    Object attributes = spec.get("attributes");
                    if (attributes instanceof List) {
                        List<?> list = (List<?>) attributes;
                        return list.stream().map(Object::toString).toArray(String[]::new);
                    }
                    return new String[]{};
                }
                
                @Override
                public RoleSpec[] getRoles() {
                    Object roles = spec.get("roles");
                    if (roles instanceof List) {
                        List<?> rolesList = (List<?>) roles;
                        return rolesList.stream().map(roleObj -> {
                            if (roleObj instanceof Map) {
                                Map<?, ?> roleMap = (Map<?, ?>) roleObj;
                                return new RoleSpec() {
                                    @Override
                                    public String getName() {
                                        Object name = roleMap.get("name");
                                        return name != null ? name.toString() : "";
                                    }
                                    
                                    @Override
                                    public String getRoleDescription() {
                                        Object description = roleMap.get("roleDescription");
                                        return description != null ? description.toString() : "";
                                    }
                                };
                            }
                            return null;
                        }).filter(role -> role != null).toArray(RoleSpec[]::new);
                    }
                    return new RoleSpec[]{};
                }
            };
        }

        private ClientSpec createDefaultClientSpec() {
            return new ClientSpec() {
                @Override
                public String getClientId() { return getName(); }
                @Override
                public boolean isEnabled() { return true; }
                @Override
                public String[] getRedirectUris() { return new String[]{"http://localhost:3000/*"}; }
                @Override
                public String[] getWebOrigins() { return new String[]{"http://localhost:3000"}; }
                @Override
                public String getDisplayedName() { return getName(); }
                @Override
                public String getDisplayedDescription() { return ""; }
                @Override
                public String getCapabilityType() { return "PUBLIC"; }
                @Override
                public String getClientExistingSecret() { return null; }
                @Override
                public String getClientSecretKey() { return null; }
                @Override
                public String getProtocol() { return "openid-connect"; }
                @Override
                public String getClientAuthenticatorType() { return "client-secret"; }
                @Override
                public Boolean getFullScopeAllowed() { return true; }
                @Override
                public Boolean getStandardFlowEnabled() { return true; }
                @Override
                public Boolean getServiceAccountsEnabled() { return false; }
                @Override
                public Boolean getDirectAccessGrantsEnabled() { return false; }
                @Override
                public Boolean getImplicitFlowEnabled() { return false; }
                @Override
                public String[] getAttributes() { return new String[]{}; }
                @Override
                public RoleSpec[] getRoles() { return new RoleSpec[]{}; }
            };
        }

        @Override
        public ClientStatus getStatus() {
            Map<String, Object> status = (Map<String, Object>) resource.getAdditionalProperties().get("status");
            ClientStatus clientStatus = new ClientStatus();
            
            if (status == null) {
                clientStatus.setState(ClientStatus.State.APPLYING);
                clientStatus.setLastApplication(Instant.now().toString());
                clientStatus.setError(null);
            } else {
                String stateStr = (String) status.get("state");
                if (stateStr != null) {
                    try {
                        clientStatus.setState(ClientStatus.State.valueOf(stateStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        clientStatus.setState(ClientStatus.State.APPLYING);
                    }
                } else {
                    clientStatus.setState(ClientStatus.State.APPLYING);
                }
                clientStatus.setLastApplication((String) status.get("lastApplication"));
                clientStatus.setError((String) status.get("error"));
            }
            
            return clientStatus;
        }

        @Override
        public void applied() {
            try {
                log.infof("Marking client as applied: %s", getName());
                
                // Update the status to mark as applied
                Map<String, Object> status = (Map<String, Object>) resource.getAdditionalProperties().get("status");
                if (status == null) {
                    status = new java.util.HashMap<>();
                    resource.getAdditionalProperties().put("status", status);
                }
                
                status.put("state", "CONFIGURED");
                status.put("lastApplication", Instant.now().toString());
                status.put("error", null);
                
                // Update the resource in Kubernetes
                k8sClient.genericKubernetesResources(crdContext)
                        .inNamespace(resource.getMetadata().getNamespace())
                        .withName(getName())
                        .replace(resource);
                
                log.infof("Successfully marked client as applied: %s", getName());
                
            } catch (Exception e) {
                log.errorf(e, "Error marking client as applied: %s", getName());
            }
        }

        @Override
        public void applying() {
            try {
                log.infof("Marking client as applying: %s", getName());
                
                Map<String, Object> status = (Map<String, Object>) resource.getAdditionalProperties().get("status");
                if (status == null) {
                    status = new java.util.HashMap<>();
                    resource.getAdditionalProperties().put("status", status);
                }
                
                status.put("state", "APPLYING");
                status.put("lastApplication", Instant.now().toString());
                status.put("error", null);
                
                k8sClient.genericKubernetesResources(crdContext)
                        .inNamespace(resource.getMetadata().getNamespace())
                        .withName(getName())
                        .replace(resource);
                
                log.infof("Successfully marked client as applying: %s", getName());
                
            } catch (Exception e) {
                log.errorf(e, "Error marking client as applying: %s", getName());
            }
        }

        @Override
        public void failed(Exception e) {
            try {
                log.errorf("Marking client as failed: %s, error: %s", getName(), e.getMessage());
                
                Map<String, Object> status = (Map<String, Object>) resource.getAdditionalProperties().get("status");
                if (status == null) {
                    status = new java.util.HashMap<>();
                    resource.getAdditionalProperties().put("status", status);
                }
                
                status.put("state", "FAILED");
                status.put("lastApplication", Instant.now().toString());
                status.put("error", e.getMessage());
                
                k8sClient.genericKubernetesResources(crdContext)
                        .inNamespace(resource.getMetadata().getNamespace())
                        .withName(getName())
                        .replace(resource);
                
                log.infof("Successfully marked client as failed: %s", getName());
                
            } catch (Exception updateError) {
                log.errorf(updateError, "Error marking client as failed: %s", getName());
            }
        }

        @Override
        public void backoff(Exception e) {
            try {
                log.infof("Marking client as backoff: %s, error: %s", getName(), e.getMessage());
                
                Map<String, Object> status = (Map<String, Object>) resource.getAdditionalProperties().get("status");
                if (status == null) {
                    status = new java.util.HashMap<>();
                    resource.getAdditionalProperties().put("status", status);
                }
                
                status.put("state", "BACKOFF");
                status.put("lastApplication", Instant.now().toString());
                status.put("error", e.getMessage());
                status.put("backoffSeconds", 30L); // 30 seconds backoff
                
                k8sClient.genericKubernetesResources(crdContext)
                        .inNamespace(resource.getMetadata().getNamespace())
                        .withName(getName())
                        .replace(resource);
                
                log.infof("Successfully marked client as backoff: %s", getName());
                
            } catch (Exception updateError) {
                log.errorf(updateError, "Error marking client as backoff: %s", getName());
            }
        }
    }
}

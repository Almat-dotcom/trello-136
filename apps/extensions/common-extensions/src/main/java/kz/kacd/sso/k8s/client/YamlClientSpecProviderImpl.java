package kz.kacd.sso.k8s.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import kz.kacd.sso.v1.ClientSpec;
import kz.kacd.sso.v1.ClientStatus;
import kz.kacd.sso.v1.RoleSpec;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlClientSpecProviderImpl implements K8sClientSpecProvider {
    private static final Logger log = Logger.getLogger(YamlClientSpecProviderImpl.class);
    
    private final String yamlDirectory;
    private final ObjectMapper yamlMapper;
    private final Map<String, Map<String, Object>> clientCache = new HashMap<>();
    
    public YamlClientSpecProviderImpl(String yamlDirectory) {
        this.yamlDirectory = yamlDirectory;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        loadAllClients();
    }
    
    private void loadAllClients() {
        try {
            Path dir = Paths.get(yamlDirectory);
            if (!Files.exists(dir)) {
                log.warnf("YAML directory does not exist: %s", yamlDirectory);
                return;
            }
            
            Files.walk(dir)
                .filter(path -> path.toString().endsWith(".yaml"))
                .forEach(this::loadClientFromFile);
                
            log.infof("Loaded %d clients from YAML files", clientCache.size());
        } catch (IOException e) {
            log.errorf(e, "Error loading clients from YAML directory: %s", yamlDirectory);
        }
    }
    
    private void loadClientFromFile(Path filePath) {
        try {
            String content = new String(Files.readAllBytes(filePath));
            Map<String, Object> yamlData = yamlMapper.readValue(content, Map.class);
            
            // Проверяем, что это Client CRD
            if ("Client".equals(yamlData.get("kind")) && "sso.kacd.kz/v1".equals(yamlData.get("apiVersion"))) {
                Map<String, Object> metadata = (Map<String, Object>) yamlData.get("metadata");
                if (metadata != null) {
                    String name = (String) metadata.get("name");
                    if (name != null) {
                        clientCache.put(name, yamlData);
                        log.debugf("Loaded client from file: %s -> %s", filePath.getFileName(), name);
                    }
                }
            }
        } catch (IOException e) {
            log.warnf(e, "Error reading YAML file: %s", filePath);
        }
    }

    @Override
    public K8sClient findByName(String name) {
        log.infof("Looking for client with name: %s in YAML files", name);
        
        Map<String, Object> clientData = clientCache.get(name);
        if (clientData != null) {
            log.infof("Found client %s in YAML cache", name);
            return new YamlClientImpl(clientData);
        } else {
            log.warnf("Client %s not found in YAML files", name);
            return null;
        }
    }

    @Override
    public void close() {
        clientCache.clear();
        log.infof("YamlClientSpecProviderImpl closed");
    }
    
    private static class YamlClientImpl implements K8sClient {
        private final Map<String, Object> clientData;
        
        public YamlClientImpl(Map<String, Object> clientData) {
            this.clientData = clientData;
        }

        @Override
        public String getName() {
            Map<String, Object> metadata = (Map<String, Object>) clientData.get("metadata");
            return metadata != null ? (String) metadata.get("name") : null;
        }

        @Override
        public String getRealm() {
            Map<String, Object> metadata = (Map<String, Object>) clientData.get("metadata");
            if (metadata != null) {
                Map<String, Object> labels = (Map<String, Object>) metadata.get("labels");
                if (labels != null && labels.containsKey("kz-kacd-realm-name")) {
                    return (String) labels.get("kz-kacd-realm-name");
                }
            }
            return "internal"; // default realm
        }

        @Override
        public ClientSpec getSpec() {
            Map<String, Object> spec = (Map<String, Object>) clientData.get("spec");
            if (spec == null) {
                log.warnf("Spec is null for client: %s", getName());
                return createDefaultClientSpec();
            }
            
            return new ClientSpec() {
                @Override
                public String getClientId() {
                    return getName(); // clientId is the same as name
                }
                
                @Override
                public boolean isEnabled() {
                    Object enabled = spec.get("enabled");
                    return enabled == null || (Boolean) enabled;
                }
                
                @Override
                public String[] getRedirectUris() {
                    Object access = spec.get("access");
                    if (access instanceof Map) {
                        Map<?, ?> accessMap = (Map<?, ?>) access;
                        Object redirectUris = accessMap.get("validRedirectUris");
                        if (redirectUris instanceof List) {
                            List<?> list = (List<?>) redirectUris;
                            return list.stream().map(Object::toString).toArray(String[]::new);
                        }
                    }
                    return new String[]{"http://localhost:3000/*"};
                }
                
                @Override
                public String[] getWebOrigins() {
                    Object access = spec.get("access");
                    if (access instanceof Map) {
                        Map<?, ?> accessMap = (Map<?, ?>) access;
                        Object webOrigins = accessMap.get("webOrigins");
                        if (webOrigins instanceof List) {
                            List<?> list = (List<?>) webOrigins;
                            return list.stream().map(Object::toString).toArray(String[]::new);
                        }
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
            ClientStatus clientStatus = new ClientStatus();
            clientStatus.setState(ClientStatus.State.APPLYING);
            clientStatus.setLastApplication(Instant.now().toString());
            clientStatus.setError(null);
            return clientStatus;
        }

        @Override
        public void applying() {
            log.infof("Marking client as applying: %s", getName());
        }

        @Override
        public void applied() {
            log.infof("Marking client as applied: %s", getName());
        }

        @Override
        public void failed(Exception e) {
            log.errorf("Marking client as failed: %s, error: %s", getName(), e.getMessage());
        }

        @Override
        public void backoff(Exception e) {
            log.infof("Marking client as backoff: %s, error: %s", getName(), e.getMessage());
        }
    }
}

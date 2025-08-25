package kz.kacd.sso.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.k8s.realm.K8sRealm;
import kz.kacd.sso.k8s.realm.K8sRealmProvider;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.resource.RealmResourceProvider;

public class K8sRealmApiResource extends BaseApiResource implements RealmResourceProvider {
    private static final Logger log = Logger.getLogger(K8sRealmApiResource.class);

    public K8sRealmApiResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
        log.infof("K8sRealmApiResource created with session: %s, realm: %s", session, realm);
    }

    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
        log.infof("K8sRealmApiResource.close() called");
    }

    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {
        return Response.ok("{\"status\":\"ok\",\"message\":\"K8sRealmApiResource is working\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }



    @POST
    @Path("realm/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRealm(@PathParam("name") String name, @QueryParam("full") String full) {
        log.infof("updateRealm called with name: %s, full: %s", name, full);
        
        // Проверки безопасности
        checkPermissions();
        checkConfig();
        
        try {
            log.infof("Attempting to get K8sRealmProvider from session");
            K8sRealmProvider k8sProvider = session.getProvider(K8sRealmProvider.class);
            log.infof("K8sRealmProvider result: %s", k8sProvider != null ? "NOT NULL" : "NULL");
            
            if (k8sProvider != null) {
                K8sRealm k8sRealm = k8sProvider.findSpec(name);
                if (k8sRealm != null) {
                    log.infof("Found realm in Kubernetes CRD: %s", name);
                    
                    k8sRealm.applying();
                    
                    kz.kacd.sso.v1.RealmSpec realmSpec = k8sRealm.getSpec();
                    if (realmSpec != null) {
                        RealmModel existingRealm = session.realms().getRealmByName(name);
                        
                        if (existingRealm != null) {
                            log.infof("Updating existing realm from CRD: %s", name);
                            updateRealmFromSpec(existingRealm, realmSpec);
                        } else {
                            log.infof("Creating new realm from CRD: %s", name);
                            createRealmFromSpec(name, realmSpec);
                        }
                        
                        k8sRealm.applied();
                        
                        log.infof("Successfully configured realm from CRD: %s", name);
                        String response = String.format(
                            "{\n" +
                            "    \"name\": \"%s\",\n" +
                            "    \"spec\": {\n" +
                            "        \"realmName\": \"%s\",\n" +
                            "        \"enabled\": true\n" +
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
                        log.warnf("Realm spec is null in Kubernetes CRD: %s", name);
                        k8sRealm.failed(new Exception("Realm spec is null"));
                    }
                } else {
                    log.warnf("Realm not found in Kubernetes CRD: %s", name);
                }
            }
            
            log.infof("Creating realm directly (no CRD found): %s", name);
            RealmModel existingRealm = session.realms().getRealmByName(name);
            
            if (existingRealm != null) {
                log.infof("Realm already exists: %s", name);
                return Response.ok()
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{\"message\":\"Realm already exists\", \"realm\":\"" + name + "\"}")
                        .build();
            } else {
                createDefaultRealm(name);
                
                String response = String.format(
                    "{\n" +
                    "    \"name\": \"%s\",\n" +
                    "    \"spec\": {\n" +
                    "        \"realmName\": \"%s\",\n" +
                    "        \"enabled\": true\n" +
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
            }
                    
        } catch (Exception e) {
            log.errorf(e, "Error updating realm: %s", name);
            
            try {
                K8sRealmProvider k8sProvider = session.getProvider(K8sRealmProvider.class);
                if (k8sProvider != null) {
                    K8sRealm k8sRealm = k8sProvider.findSpec(name);
                    if (k8sRealm != null) {
                        k8sRealm.failed(e);
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

    private void createRealmFromSpec(String name, kz.kacd.sso.v1.RealmSpec spec) {
        RealmModel newRealm = session.realms().createRealm(name);
        newRealm.setName(name);
        newRealm.setEnabled(true);
        newRealm.setSslRequired(org.keycloak.common.enums.SslRequired.NONE);
        
        org.keycloak.models.RoleModel defaultRole = newRealm.getRole("default-roles-" + name);
        if (defaultRole == null) {
            defaultRole = newRealm.addRole("default-roles-" + name);
        }
        newRealm.setDefaultRole(defaultRole);
        
        log.infof("Created realm from CRD: %s", name);
    }

    private void updateRealmFromSpec(RealmModel realm, kz.kacd.sso.v1.RealmSpec spec) {
        realm.setEnabled(true);
        log.infof("Updated realm from CRD: %s", realm.getName());
    }

    private void createDefaultRealm(String name) {
        RealmModel newRealm = session.realms().createRealm(name);
        newRealm.setName(name);
        newRealm.setEnabled(true);
        newRealm.setSslRequired(org.keycloak.common.enums.SslRequired.NONE);
        
        org.keycloak.models.RoleModel defaultRole = newRealm.getRole("default-roles-" + name);
        if (defaultRole == null) {
            defaultRole = newRealm.addRole("default-roles-" + name);
        }
        newRealm.setDefaultRole(defaultRole);
        
        log.infof("Created default realm: %s", name);
    }

}




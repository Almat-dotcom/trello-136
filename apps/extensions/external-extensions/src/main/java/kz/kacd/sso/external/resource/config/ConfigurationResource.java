package kz.kacd.sso.external.resource.config;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import kz.kacd.sso.external.resource.BaseAdminResource;
import kz.kacd.sso.k8s.client.K8sClientSpecProvider;
import kz.kacd.sso.k8s.client.K8sClient;
import kz.kacd.sso.client.config.ClientConfigurer;

public class ConfigurationResource extends BaseAdminResource {
    private static final Logger log = Logger.getLogger(ConfigurationResource.class);

    protected ConfigurationResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
    }

    @POST
    @Path("realm/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response configureRealm(@PathParam("name") String name, @QueryParam("full") String full) {
        log.infof("Configuring realm: %s, full: %s", name, full);
        
        // Placeholder implementation - will be implemented when dependencies are available
        return Response.ok("{\"status\":\"configured\",\"realm\":\"" + name + "\"}").build();
    }

    @POST
    @Path("federation/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response configureFederation(@PathParam("name") String name) {
        hasReadPermissions();
        log.infof("Configuring federation: %s", name);
        
        // Placeholder implementation - will be implemented when dependencies are available
        return Response.ok("{\"status\":\"configured\",\"federation\":\"" + name + "\"}").build();
    }

    @POST
    @Path("client/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response configureClient(@PathParam("name") String name) {
        try {
            log.info("START ALMAT CLIENT");
            log.infof("Configuring client: %s", name);
            
            // Используем существующий ClientConfigurer
            K8sClientSpecProvider k8sProvider = session.getProvider(K8sClientSpecProvider.class);
            if (k8sProvider == null) {
                log.error("K8sClientSpecProvider not available!");
                return Response.ok("{\"status\":\"error\",\"message\":\"K8sClientSpecProvider not available\"}").build();
            }
            
            K8sClient k8sClient = k8sProvider.findByName(name);
            if (k8sClient == null) {
                log.warnf("Client %s not found in Kubernetes!", name);
                return Response.ok("{\"status\":\"not_found\",\"client\":\"" + name + "\"}").build();
            }
            
            if (!k8sClient.getRealm().equals(realm.getName())) {
                log.warnf("Client %s belongs to realm %s, but current realm is %s!", name, k8sClient.getRealm(), realm.getName());
                return Response.ok("{\"status\":\"wrong_realm\",\"client\":\"" + name + "\"}").build();
            }
            
            // Используем существующий ClientConfigurer для создания клиента
            ClientConfigurer configurer = session.getProvider(ClientConfigurer.class);
            if (configurer == null) {
                log.error("ClientConfigurer not available!");
                return Response.ok("{\"status\":\"error\",\"message\":\"ClientConfigurer not available\"}").build();
            }
            
            configurer.configure(realm, k8sClient.getName(), k8sClient.getSpec());
            log.infof("Successfully configured client: %s", name);
            
            return Response.ok("{\"status\":\"configured\",\"client\":\"" + name + "\"}").build();
        } catch (Exception e) {
            log.error("Error on configuring client!", e);
            throw new InternalServerErrorException(e);
        }
    }
}

//package kz.kacd.sso.external.resource.profile;
//
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import kz.kacd.sso.external.resource.BaseAdminResource;
//import kz.kacd.sso.external.k8s.realm.K8sRealm;
//import kz.kacd.sso.external.k8s.realm.K8sRealmProvider;
//import kz.kacd.sso.external.v1.RealmStatus;
//import org.jboss.logging.Logger;
//import org.keycloak.models.KeycloakSession;
//import org.keycloak.models.RealmModel;
//
//public class ConfigurationResource extends BaseAdminResource {
//    private static final Logger log = Logger.getLogger(ConfigurationResource.class);
//
//    protected ConfigurationResource(KeycloakSession session, RealmModel realm) {
//        super(session, realm);
//    }
//
//    @POST
//    @Path("client/{name}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response configureClient(@PathParam("name") String name) {
////        checkPermissions();
////        checkConfig();
//        try {
//            log.info("ALMAT CLIENT START");
//            K8sRealmProvider k8s = session.getProvider(K8sRealmProvider.class);
//            if (k8s == null) {
//                log.warn("K8sRealmProvider not found, returning mock response");
//                return Response.ok().type(MediaType.APPLICATION_JSON).entity("{\"status\":\"mock\",\"message\":\"Provider not available\"}").build();
//            }
//            K8sRealm spec = k8s.findSpec(name);
//            log.info("ALMAT CLIENT CONFIGURING");
//            return Response.ok().type(MediaType.APPLICATION_JSON).entity("{\"status\":\"success\",\"message\":\"Client configuration completed\"}").build();
//        } catch (Exception e) {
//            log.error("ALMAT Error on configuring client!", e);
//            throw new RuntimeException(e);
//        }
//    }
//}

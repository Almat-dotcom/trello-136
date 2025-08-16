package kz.kacd.sso.resource.config;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.client.config.ClientConfigurer;
import kz.kacd.sso.federation.FederationConfigurer;
import kz.kacd.sso.k8s.client.K8sClient;
import kz.kacd.sso.k8s.client.K8sClientSpecProvider;
import kz.kacd.sso.k8s.federation.K8sFederation;
import kz.kacd.sso.k8s.federation.K8sFederationProvider;
import kz.kacd.sso.k8s.realm.K8sRealm;
import kz.kacd.sso.k8s.realm.K8sRealmProvider;
import kz.kacd.sso.realm.config.KeycloakRealmConfigurer;
import kz.kacd.sso.resource.common.ConfigResourceType;
import org.jboss.logging.Logger;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.ModelToRepresentation;

public class ConfigurationResource extends BaseConfigAdminResource {
    private static final Logger log = Logger.getLogger(ConfigurationResource.class);

    protected ConfigurationResource(RealmModel realm) {
        super(realm);
    }

    @POST
    @Path("realm/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response configureRealm(@PathParam("name") String name, @QueryParam("full") String full) {
        checkPermissions();
        checkConfig();

        K8sRealmProvider k8s = session.getProvider(K8sRealmProvider.class);
        K8sRealm spec = k8s.findSpec(name);
        if (spec == null) {
            throw new NotFoundException("Realm with name " + name + " not found in k8s!");
        }

        // Получаем статус через reflection для изолированных классов
        Object initialStatus = getStatusViaReflection(spec);
        spec.applying();
        KeycloakRealmConfigurer configurer = session.getProvider(KeycloakRealmConfigurer.class);

        try {
            RealmModel realm;
            if (full != null && full.equals("true")) {
                realm = configurer.configureWithDependencies(spec);
            } else {
                realm = configurer.configure(spec);
            }

            spec.applied();
            adminEvent.resource(ConfigResourceType.REALM_CONFIG.name())
                    .operation(OperationType.UPDATE)
                    .resourcePath(session.getContext().getUri())
                    .representation(spec.getSpec())
                    .success();

            return Response.ok(ModelToRepresentation.toBriefRepresentation(realm)).build();
        } catch (Exception e) {
            log.error("Error on configuring realm!", e);
            if (initialStatus != null && isBackOffState(initialStatus)) {
                spec.failed(e);
            } else {
                spec.toBackOff(e);
            }
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path("federation/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response configureFederation(@PathParam("name") String name) {
        checkPermissions();
        checkConfig();

        K8sFederationProvider k8s = session.getProvider(K8sFederationProvider.class);
        K8sFederation spec = k8s.findByName(name);
        if (spec == null || spec.getRealm() == null || !spec.getRealm().equals(session.getContext().getRealm().getName())) {
            log.infof("Federation %s with realm %s not found!", name, session.getContext().getRealm().getName());
            throw new NotFoundException("Federation " + name + " not found for this realm!");
        }

        Object initial = getStatusViaReflection(spec);
        spec.applying();

        FederationConfigurer configurer = session.getProvider(FederationConfigurer.class);

        try {
            configurer.configure(session.getContext().getRealm(), spec.getSpec());
            spec.applied();
            adminEvent.resource(ConfigResourceType.FEDERATION_CONFIG.name())
                    .operation(OperationType.UPDATE)
                    .resourcePath(session.getContext().getUri())
                    .representation(spec.getSpec())
                    .success();
            return Response.ok(spec.getSpec()).build();
        } catch (Exception e) {
            log.error("Error on configuring federation!", e);
            if (initial != null && isBackOffState(initial)) {
                spec.failed(e);
            } else {
                spec.backoff(e);
            }
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path("client/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response configureClient(@PathParam("name") String name) {
//        checkPermissions();
//        checkConfig();

        K8sClientSpecProvider k8s = session.getProvider(K8sClientSpecProvider.class);
        K8sClient spec = k8s.findByName(name);
        if (spec == null || spec.getRealm() == null || !spec.getRealm().equals(session.getContext().getRealm().getName())) {
            log.infof("Client %s with realm %s not found!", name, session.getContext().getRealm().getName());
            throw new NotFoundException("Client " + name + " for current realm not found!");
        }

        Object initial = getStatusViaReflection(spec);
        spec.applying();

        ClientConfigurer configurer = session.getProvider(ClientConfigurer.class);
        try {
            configurer.configure(realm, spec.getName(), spec.getSpec());
            spec.applied();
            adminEvent.resource(ConfigResourceType.CLIENT_CONFIG.name())
                    .operation(OperationType.UPDATE)
                    .resourcePath(session.getContext().getUri())
                    .representation(spec.getSpec())
                    .success();
            return Response.ok(spec.getSpec()).build();
        } catch (Exception e) {
            log.error("Error on configuring client!", e);
            if (initial != null && isBackOffState(initial)) {
                spec.failed(e);
            } else {
                spec.backoff(e);
            }
            throw new InternalServerErrorException(e);
        }
    }

    // Вспомогательные методы для работы с изолированными классами
    private Object getStatusViaReflection(Object spec) {
        try {
            return spec.getClass().getMethod("getStatus").invoke(spec);
        } catch (Exception e) {
            log.warn("Failed to get status via reflection", e);
            return null;
        }
    }

    private boolean isBackOffState(Object status) {
        try {
            if (status == null) return false;
            Object state = status.getClass().getMethod("getState").invoke(status);
            if (state == null) return false;

            String stateStr = state.toString();
            return stateStr.equals("BACK_OFF") || stateStr.equals("BACKOFF");
        } catch (Exception e) {
            log.warn("Failed to check backoff state via reflection", e);
            return false;
        }
    }
}

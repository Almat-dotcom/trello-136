package kz.kacd.sso.resource.config;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.BaseAdminResource;
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
import kz.kacd.sso.v1.ClientStatus;
import kz.kacd.sso.v1.FederationStatus;
import kz.kacd.sso.v1.RealmStatus;
import org.jboss.logging.Logger;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.ModelToRepresentation;

public class ConfigurationResource extends BaseAdminResource {
    private static final Logger log = Logger.getLogger(ConfigurationResource.class);

    protected ConfigurationResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
    }

    @GET
    @Path("ping")
    @Produces(MediaType.TEXT_PLAIN)
    public Response ping() {
        return Response.ok("ok").build();
    }

    @OPTIONS
    @Path("")
    public Response corsPreflightRoot(@Context jakarta.ws.rs.core.HttpHeaders headers) {
        String origin = headers.getHeaderString("Origin");
        return Response.noContent()
                .header("Access-Control-Allow-Origin", origin == null ? "*" : origin)
                .header("Vary", "Origin")
                .header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH")
                .header("Access-Control-Allow-Headers", "Authorization,Content-Type,Accept,Origin")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }

    @OPTIONS
    @Path("client/{name}")
    public Response corsPreflightClient(@Context jakarta.ws.rs.core.HttpHeaders headers) {
        String origin = headers.getHeaderString("Origin");
        return Response.noContent()
                .header("Access-Control-Allow-Origin", origin == null ? "*" : origin)
                .header("Vary", "Origin")
                .header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH")
                .header("Access-Control-Allow-Headers", "Authorization,Content-Type,Accept,Origin")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }

    @OPTIONS
    @Path("realm/{name}")
    public Response corsPreflightRealm(@Context jakarta.ws.rs.core.HttpHeaders headers) {
        String origin = headers.getHeaderString("Origin");
        return Response.noContent()
                .header("Access-Control-Allow-Origin", origin == null ? "*" : origin)
                .header("Vary", "Origin")
                .header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH")
                .header("Access-Control-Allow-Headers", "Authorization,Content-Type,Accept,Origin")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }

    @OPTIONS
    @Path("federation/{name}")
    public Response corsPreflightFederation(@Context jakarta.ws.rs.core.HttpHeaders headers) {
        String origin = headers.getHeaderString("Origin");
        return Response.noContent()
                .header("Access-Control-Allow-Origin", origin == null ? "*" : origin)
                .header("Vary", "Origin")
                .header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH")
                .header("Access-Control-Allow-Headers", "Authorization,Content-Type,Accept,Origin")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }

    @OPTIONS
    @Path("{any:.*}")
    public Response corsPreflight(@Context jakarta.ws.rs.core.HttpHeaders headers) {
        String origin = headers.getHeaderString("Origin");
        return Response.noContent()
                .header("Access-Control-Allow-Origin", origin == null ? "*" : origin)
                .header("Vary", "Origin")
                .header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH")
                .header("Access-Control-Allow-Headers", "Authorization,Content-Type,Accept,Origin")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }


    @POST
    @Path("realm/{name}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Produces(MediaType.APPLICATION_JSON)
    public Response configureRealm(@PathParam("name") String name, @QueryParam("full") String full) {
//        checkPermissions();
//        checkConfig();

        K8sRealmProvider k8s = session.getProvider(K8sRealmProvider.class);
        K8sRealm spec = k8s.findSpec(name);
        if (spec == null) {
            throw new NotFoundException("Realm with name " + name + " not found in k8s!");
        }
        RealmStatus initialStatus = spec.getStatus();
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
            if (initialStatus != null && initialStatus.getState() == RealmStatus.State.BACK_OFF) {
                spec.failed(e);
            } else {
                spec.toBackOff(e);
            }
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path("federation/{name}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Produces(MediaType.APPLICATION_JSON)
    public Response configureFederation(@PathParam("name") String name) {
//        checkPermissions();
//        checkConfig();

        K8sFederationProvider k8s = session.getProvider(K8sFederationProvider.class);
        K8sFederation spec = k8s.findByName(name);
        if (spec == null || spec.getRealm() == null || !spec.getRealm().equals(session.getContext().getRealm().getName())) {
            log.infof("Federation %s with realm %s not found!", name, session.getContext().getRealm().getName());
            throw new NotFoundException("Federation " + name + " not found for this realm!");
        }
        FederationStatus initial = spec.getStatus();
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
            if (initial != null && initial.getState() == FederationStatus.State.BACK_OFF) {
                spec.failed(e);
            } else {
                spec.backoff(e);
            }
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path("client/{name}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    @Produces(MediaType.APPLICATION_JSON)
    public Response configureClient(@PathParam("name") String name) {
//        checkPermissions();
//        checkConfig();
        try {
            log.info("ALMAT CLIENT START");
            K8sClientSpecProvider k8s = session.getProvider(K8sClientSpecProvider.class);
            String enabledK8s = System.getenv("ENABLED_K8S");
            if (enabledK8s != null && enabledK8s.equalsIgnoreCase("false")) {
                log.warn("K8s is disabled by env, returning mock response");
                return Response.ok().type(MediaType.APPLICATION_JSON).entity("{\"status\":\"mock\",\"message\":\"K8s disabled\"}").build();
            }
            if (k8s == null) {
                log.warn("K8sClientSpecProvider not found, returning mock response");
                return Response.ok().type(MediaType.APPLICATION_JSON).entity("{\"status\":\"mock\",\"message\":\"Provider not available\"}").build();
            }
            K8sClient spec = k8s.findByName(name);
            if (spec == null || spec.getRealm() == null || !spec.getRealm().equals(session.getContext().getRealm().getName())) {
                log.infof("Client %s with realm %s not found!", name, session.getContext().getRealm().getName());
                throw new NotFoundException("Client " + name + " for current realm not found!");
            }
            ClientStatus initial = spec.getStatus();
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
                if (initial != null && initial.getState() == ClientStatus.State.BACKOFF) {
                    spec.failed(e);
                } else {
                    spec.backoff(e);
                }
                throw new InternalServerErrorException(e);
            }
        } catch (Exception e) {
            log.error("ALMAT Error on configuring client!", e);
            throw new RuntimeException(e);
        }
    }
}

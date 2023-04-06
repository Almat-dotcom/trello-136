package kz.kacd.sso.resource.profile;

import com.google.auto.service.AutoService;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmProfileResourceProviderFactory.class)
public class RealmProfileResourceProviderFactory implements RealmResourceProviderFactory {
    public static final String PROVIDER_ID = "profile";
    private static final Logger log = Logger.getLogger(RealmProfileResourceProviderFactory.class);

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new RealmProfileResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // Nothing to init
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof RealmModel.RealmPostCreateEvent) {
                log.debugf("Realm post create event captured.");
                realmPostCreate((RealmModel.RealmPostCreateEvent) event);
            } else if (event instanceof PostMigrationEvent) {
                log.debugf("Post migration event captured.");
                KeycloakModelUtils.runJobInTransaction(factory, this::initRoles);
            }
        });
    }

    private void initRoles(KeycloakSession session) {
        RealmManager manager = new RealmManager(session);
        session.realms()
                .getRealmsStream()
                .forEach(realm -> {
                    ClientModel client = realm.getMasterAdminClient();
                    if (
                            client.getRole(ProfileAdminAuth.QUERY_PROFILES_ROLE) == null
                                    || client.getRole(ProfileAdminAuth.UPDATE_LOGIN_OPTIONS_ROLE) == null
                    ) {
                        addMasterAdminRoles(manager, realm);
                    }
                    if (!realm.getName().equals(Config.getAdminRealm())) {
                        client = realm.getClientByClientId(manager.getRealmAdminClientId(realm));
                        if (
                                client.getRole(ProfileAdminAuth.QUERY_PROFILES_ROLE) == null
                                        || client.getRole(ProfileAdminAuth.UPDATE_LOGIN_OPTIONS_ROLE) == null
                        ) {
                            addRealmAdminRoles(manager, realm);
                        }
                    }
                });
    }

    private void realmPostCreate(RealmModel.RealmPostCreateEvent event) {
        RealmModel realm = event.getCreatedRealm();
        RealmManager manager = new RealmManager(event.getKeycloakSession());
        addMasterAdminRoles(manager, realm);
        if (!realm.getName().equals(Config.getAdminRealm())) {
            addRealmAdminRoles(manager, realm);
        }
    }

    private void addMasterAdminRoles(RealmManager manager, RealmModel realm) {
        RealmModel master = manager.getRealmByName(Config.getAdminRealm());
        RoleModel admin = master.getRole(AdminRoles.ADMIN);
        ClientModel client = realm.getMasterAdminClient();

        addRoles(client, admin);
    }

    private void addRealmAdminRoles(RealmManager manager, RealmModel realm) {
        ClientModel client = realm.getClientByClientId(manager.getRealmAdminClientId(realm));
        RoleModel admin = client.getRole(AdminRoles.REALM_ADMIN);

        addRoles(client, admin);
    }

    private void addRoles(ClientModel client, RoleModel parent) {
        addRole(ProfileAdminAuth.QUERY_PROFILES_ROLE, client, parent);
        addRole(ProfileAdminAuth.UPDATE_LOGIN_OPTIONS_ROLE, client, parent);
    }

    private void addRole(String name, ClientModel client, RoleModel parent) {
        if (client.getRole(name) == null) {
            RoleModel role = client.addRole(name);
            role.setDescription("${role_" + name + "}");
            parent.addCompositeRole(role);
        }
    }

    @Override
    public void close() {
        // Nothing to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}

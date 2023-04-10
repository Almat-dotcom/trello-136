package kz.kacd.sso.external.resource;

import com.google.auto.service.AutoService;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.resource.common.OrganizationAdminAuth;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

/**
 * Resource provider factory for organization admin resource.
 * </>
 * It provides login on admin events to provide keycloak with logically correct organization model.
 */
@AutoService(RealmResourceProviderFactory.class)
public class OrganizationResourceProviderFactory implements RealmResourceProviderFactory {
    public static final String PROVIDER_ID = "orgs";
    private static final Logger log = Logger.getLogger(OrganizationResourceProviderFactory.class);

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new OrganizationsResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // This provider has no configuration.
    }

    /**
     * Registers event listeners.
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(
                event -> {
                    if (event instanceof RealmModel.RealmPostCreateEvent) {
                        // On every realm creation we should add set of admin roles for organizations endpoint.
                        log.debug("Realm post create event captured.");
                        realmPostCreate((RealmModel.RealmPostCreateEvent) event);
                    } else if (event instanceof RealmModel.RealmRemovedEvent) {
                        // On every realm removal event we should remove all its organizations.
                        log.debug("Realm removed captured.");
                        realmPostRemove((RealmModel.RealmRemovedEvent) event);
                    } else if (event instanceof PostMigrationEvent) {
                        // After migration, we should initialize all roles for organization endpoint.
                        log.debug("Post migration event captured.");
                        KeycloakModelUtils.runJobInTransaction(factory, this::initRoles);
                    } else if (event instanceof UserModel.UserRemovedEvent) {
                        // After user removal, we should remove all organization membership of it.
                        log.debug("Post user removal event captured.");
                        postUserRemove((UserModel.UserRemovedEvent) event);
                    } else if (event instanceof OrganizationModel.OrganizationCreatedEvent) {
                        log.debug("Organization created event captured.");
                        log.debug("You need to provide some implementation of organization event handling!");
                    } else if (event instanceof OrganizationModel.OrganizationRemovedEvent) {
                        log.debug("Organization removed event captured.");
                        postOrganizationRemoved((OrganizationModel.OrganizationRemovedEvent) event);
                    }
                }
        );
    }

    private void initRoles(KeycloakSession session) {
        RealmManager manager = new RealmManager(session);
        session
                .realms()
                .getRealmsStream()
                .forEach(
                        realm -> {
                            ClientModel client = realm.getMasterAdminClient();
                            if (client.getRole(OrganizationAdminAuth.ORGANIZATION_VIEW_ROLE) == null
                                    || client.getRole(OrganizationAdminAuth.ORGANIZATION_MANAGE_ROLE) == null
                                    || client.getRole(OrganizationAdminAuth.ORGANIZATION_CREATE_ROLE) == null) {
                                addMasterAdminRoles(manager, realm);
                            }
                            if (!realm.getName().equals(Config.getAdminRealm())) {
                                client = realm.getClientByClientId(manager.getRealmAdminClientId(realm));
                                if (client.getRole(OrganizationAdminAuth.ORGANIZATION_VIEW_ROLE) == null
                                        || client.getRole(OrganizationAdminAuth.ORGANIZATION_MANAGE_ROLE) == null
                                        || client.getRole(OrganizationAdminAuth.ORGANIZATION_CREATE_ROLE) == null) {
                                    addRealmAdminRoles(manager, realm);
                                }
                            }
                        });
        log.infof("NEW BIN: %s", session.getProvider(OrganizationProvider.class).generateNonResidentOrganizationBin());
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

        String[] names = new String[]{
                OrganizationAdminAuth.ORGANIZATION_VIEW_ROLE,
                OrganizationAdminAuth.ORGANIZATION_MANAGE_ROLE
        };

        for (String name : names) {
            addRole(name, client, parent, true);
        }

        addRole(OrganizationAdminAuth.ORGANIZATION_CREATE_ROLE, client, parent, false);
    }

    private void addRole(String name, ClientModel client, RoleModel parent, boolean composite) {
        if (client.getRole(name) == null) {
            RoleModel role = client.addRole(name);
            role.setDescription("${role_" + name + "}");
            if (composite) {
                parent.addCompositeRole(role);
            }
        }
    }

    private void realmPostRemove(RealmModel.RealmRemovedEvent event) {
        event.getKeycloakSession()
                .getProvider(OrganizationProvider.class)
                .removeOrganizations(event.getRealm());
    }

    private void postUserRemove(UserModel.UserRemovedEvent event) {
        OrganizationProvider orgs = event.getKeycloakSession().getProvider(OrganizationProvider.class);
        RealmModel realm = event.getRealm();
        UserModel user = event.getUser();
        orgs.getUserOrganizations(realm, user)
                .forEach(organization -> {
                    if (organization.getPosition(user).getName().equals(PositionModel.HEAD)) {
                        orgs.removeOrganization(realm, organization.getId());
                    } else {
                        organization.removePosition(organization.getPosition(user));
                    }
                });
    }

    private void postOrganizationRemoved(OrganizationModel.OrganizationRemovedEvent event) {
        event.getOrganization().getPositions()
                .forEach(position -> {
                    UserModel user = event.getKeycloakSession().users().getUserById(event.getRealm(), position.getId());
                    if (user != null) {
                        user.setEnabled(false);
                    }
                });
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

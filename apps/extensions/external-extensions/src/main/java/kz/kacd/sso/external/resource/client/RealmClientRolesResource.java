package kz.kacd.sso.external.resource.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.jboss.logging.Logger;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.util.List;
import java.util.stream.Collectors;

public class RealmClientRolesResource extends BaseAdminResource {
    private static final Logger log = Logger.getLogger(RealmClientRolesResource.class);

    protected RealmClientRolesResource(KeycloakSession session, RealmModel realm) {
        super(session, realm);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findClients() {
        hasReadPermissions();

        log.debugf("Finding clients for realm {} ...", realm.getName());
        List<RealmClientRepresentation> representations = session.clients().getClientsStream(realm)
                .filter(ClientModel::isEnabled)
                .filter(it -> !protectedClients(it.getClientId()))
                .map(it -> new RealmClientRepresentation(it.getClientId(), it.getName(), it.getDescription()))
                .collect(Collectors.toList());

        return Response.ok(representations).build();
    }

    @GET
    @Path("{clientId}/role")
    public Response findRoles(@PathParam("clientId") String clientId) {
        hasReadPermissions();

        log.debugf("Finding roles for client {} in realm {} ...", clientId, realm.getName());
        ClientModel client = session.clients().getClientByClientId(realm, clientId);
        if (client == null || protectedClients(client.getClientId())) {
            throw new NotFoundException("Client not found!");
        }

        List<RealmClientRoleRepresentation> representations = client.getRolesStream()
                .map(it -> new RealmClientRoleRepresentation(it.getName(), it.getDescription()))
                .collect(Collectors.toList());
        return Response.ok(representations).build();
    }

    private boolean protectedClients(String clientId) {
        return clientId.equals("realm-management")
               || clientId.equals("admin-cli")
               || clientId.equals("broker")
               || clientId.equals("realm-admin")
               || clientId.equals("security-admin-console");
    }
}

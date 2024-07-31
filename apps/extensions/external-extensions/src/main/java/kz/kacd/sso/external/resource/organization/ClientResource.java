package kz.kacd.sso.external.resource.organization;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.representation.ClientSecretRepresentation;
import kz.kacd.sso.external.representation.ClientUpdateCommand;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.RealmModel;

import javax.validation.Valid;

public class ClientResource extends BaseAdminResource {

    private final OrganizationModel org;
    private final ClientModel client;

    ClientResource(RealmModel realm, OrganizationModel org, ClientModel client) {
        super(realm);
        this.org = org;
        this.client = client;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Valid ClientUpdateCommand command) {
        org.updateClient(client.getClientId(), command.getDescription(), command.getActive());
        return Response.accepted("{}").build();
    }

    @POST
    @Path("credentials")
    @Produces(MediaType.APPLICATION_JSON)
    public ClientSecretRepresentation reset() {
        return new ClientSecretRepresentation(org.resetClientSecret(client.getClientId()));
    }

    @POST
    @Path("scope/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addScope(@PathParam("name") String name) {
        if (name == null) {
            throw new BadRequestException();
        }
        ClientScopeModel scope = session.clientScopes().getClientScopesStream(realm)
                .filter(it -> it.getName().equals(name))
                .findFirst().orElseThrow(NotFoundException::new);
        client.addClientScope(scope, false);
        return Response.accepted("{}").build();
    }

    @DELETE
    @Path("scope/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeScope(@PathParam("name") String name) {
        if (name == null) {
            throw new BadRequestException();
        }
        ClientScopeModel scope = client.getClientScopes(false)
                .values()
                .stream()
                .filter(it -> it.getName().equals(name))
                .findFirst().orElseThrow(NotFoundException::new);
        client.removeClientScope(scope);
        return Response.accepted("{}").build();
    }
}

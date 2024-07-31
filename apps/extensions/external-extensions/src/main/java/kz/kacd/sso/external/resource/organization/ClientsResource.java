package kz.kacd.sso.external.resource.organization;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.jpa.exception.ClientAlreadyExistsException;
import kz.kacd.sso.external.model.jpa.exception.ClientNotFoundException;
import kz.kacd.sso.external.model.jpa.exception.InvalidListOfScopesException;
import kz.kacd.sso.external.representation.ClientSecretRepresentation;
import kz.kacd.sso.external.representation.Content;
import kz.kacd.sso.external.representation.CreateClientCommand;
import kz.kacd.sso.external.representation.OrganizationClientRepresentation;
import kz.kacd.sso.external.resource.BaseAdminResource;
import org.jboss.logging.Logger;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;

import javax.validation.Valid;
import java.util.stream.Collectors;

public class ClientsResource extends BaseAdminResource {
    private static final Logger log = Logger.getLogger(ClientsResource.class);

    private final OrganizationModel model;

    ClientsResource(RealmModel realm, OrganizationModel model) {
        super(realm);
        this.model = model;
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Content<OrganizationClientRepresentation> findAll(@QueryParam("active") Boolean active) {
        checkViewPermissions();
        try {
            return new Content<>(
                    model.getClients(active)
                            .stream()
                            .map(OrganizationClientRepresentation::of)
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            log.error("Error on finding clients", e);
            throw e;
        }
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ClientSecretRepresentation create(@Valid CreateClientCommand command) {
        checkEditPermissions();
        try {
            String secret = model.createClient(command.getClientId(), command.getDescription(), command.getScopes());
            log.infof("Created new client %s for org %s.", command.getClientId(), model.getBin());
            return new ClientSecretRepresentation(secret);
        } catch (ClientAlreadyExistsException | InvalidListOfScopesException e) {
            log.warnf("Error on creating client: ", e.getMessage());
            throw new BadRequestException();
        }
    }

    @Path("{clientId}")
    public ClientResource client(@PathParam("clientId") String clientId) {
        try {
            ClientModel client = model.getClient(clientId);
            return setupResource(new ClientResource(realm, model, client));
        } catch (ClientNotFoundException e) {
            throw new NotFoundException();
        }
    }
}

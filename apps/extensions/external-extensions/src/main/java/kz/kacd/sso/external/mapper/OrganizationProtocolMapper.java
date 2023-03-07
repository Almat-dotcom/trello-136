package kz.kacd.sso.external.mapper;

import com.google.auto.service.AutoService;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.OrganizationProvider;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.page.ExternalRegistrationPage;
import kz.kacd.sso.external.representation.UserOrganizationRepresentation;
import org.keycloak.models.*;
import org.keycloak.protocol.ProtocolMapper;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AutoService(ProtocolMapper.class)
public class OrganizationProtocolMapper extends AbstractOIDCProtocolMapper
        implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    private static final String PROVIDER_ID = "org-protocol-mapper";
    private static final String CLAIM = "org";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, OrganizationProtocolMapper.class);
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return "User organization";
    }

    @Override
    public String getHelpText() {
        return "Maps organization of user if this user's clientType is legal.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession, ClientSessionContext clientSessionCtx) {
        UserModel user = userSession.getUser();
        String type = user.getFirstAttribute(ExternalRegistrationPage.FIELD_CLIENT_TYPE);
        if (type != null && type.equals(ExternalRegistrationPage.CLIENT_LEGAL)) {
            RealmModel realm = keycloakSession.getContext().getRealm();
            OrganizationProvider orgs = keycloakSession.getProvider(OrganizationProvider.class);
            Optional<OrganizationModel> organization = orgs.getUserOrganizations(realm, user).findFirst();

            if (!organization.isPresent()) {
                return;
            }

            PositionModel position = organization.get().getPosition(user);

            UserOrganizationRepresentation representation = UserOrganizationRepresentation.from(
                    organization.get(),
                    position
            );

            token.getOtherClaims().put(CLAIM, representation);
        }
    }
}

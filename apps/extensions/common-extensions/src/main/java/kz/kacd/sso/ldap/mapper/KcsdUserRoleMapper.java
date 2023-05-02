package kz.kacd.sso.ldap.mapper;

import com.google.auto.service.AutoService;
import com.google.common.collect.Streams;
import org.keycloak.models.*;
import org.keycloak.protocol.ProtocolMapper;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(ProtocolMapper.class)
public class KcsdUserRoleMapper extends AbstractOIDCProtocolMapper
        implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "kcsd-user-role-mapper";

    private static final String CLAIM = "kcsd_roles";
    private static final String IGNORE_CLIENT = "realm-management";
    private static final String REALM = "realm";

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(CONFIG_PROPERTIES, KcsdUserRoleMapper.class);
    }

    @Override
    protected void setClaim(
            IDToken token,
            ProtocolMapperModel mappingModel,
            UserSessionModel userSession,
            KeycloakSession keycloakSession,
            ClientSessionContext clientSessionCtx
    ) {
        UserModel user = userSession.getUser();
        RealmModel realm = userSession.getRealm();

        Stream<ClientModel> clientModelStream = keycloakSession.clients().getClientsStream(realm);
        Map<String, String> clients = clientModelStream
                .collect(Collectors.toMap(ClientModel::getId, ClientModel::getClientId));

        Map<String, List<RoleModel>> roleModelStream = getAllRoles(user)
                .collect(Collectors.groupingBy(RoleModel::getContainerId));
        Map<String, List<String>> roles = roleModelStream.entrySet().stream()
                .map(it ->
                        Tuples.of(
                                it.getKey(),
                                it.getValue().stream().map(RoleModel::getName)
                                        .distinct()
                                        .collect(Collectors.toList())
                        )
                ).collect(Collectors.toMap(Tuple2::getT1, Tuple2::getT2));

        Map<String, List<String>> rolesClaimValue = new HashMap<>();
        roles.forEach((containerId, r) -> {
            if (clients.containsKey(containerId) && !clients.get(containerId).equals(IGNORE_CLIENT)) {
                rolesClaimValue.put(clients.get(containerId), r);
            }

            if (!clients.containsKey(containerId)) {
                if (rolesClaimValue.containsKey(REALM)) {
                    rolesClaimValue.get(REALM).addAll(r);
                } else {
                    rolesClaimValue.put(REALM, r);
                }
            }
        });

        token.getOtherClaims().put(CLAIM, rolesClaimValue);
    }

    private Stream<RoleModel> getAllRoles(UserModel user) {
        Stream<GroupModel> groups = user.getGroupsStream();
        Stream<RoleModel> directRoles = user.getRoleMappingsStream();
        Stream<RoleModel> groupRoles = groups.flatMap(GroupModel::getRoleMappingsStream);

        return Streams.concat(directRoles, groupRoles)
                .flatMap(role -> {
                    if (role.isComposite()) {
                        return role.getCompositesStream();
                    }
                    return Stream.of(role);
                });
    }

    @Override
    public int getPriority() {
        return ProtocolMapperUtils.PRIORITY_ROLE_MAPPER;
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return "KCSD User Role Mapper";
    }

    @Override
    public String getHelpText() {
        return "Maps user roles in KCSD style";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}

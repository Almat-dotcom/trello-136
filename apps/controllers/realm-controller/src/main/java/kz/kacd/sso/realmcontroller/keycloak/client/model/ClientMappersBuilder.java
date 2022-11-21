package kz.kacd.sso.realmcontroller.keycloak.client.model;

import org.keycloak.representations.idm.ProtocolMapperRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientMappersBuilder {
    private static final String FIRST_NAME_NAME = "firstName";
    private static final String LAST_NAME_NAME = "lastName";
    private static final String MODEL_MAPPER = "oidc-usermodel-property-mapper";
    private static final String MIDDLE_NAME_NAME = "middle_name";
    private static final String OIDC = "openid-connect";
    private static final String ATTR_MAPPER = "oidc-usermodel-attribute-mapper";
    private static final String ADD_TO_ACCESS_TOKEN = "access.token.claim";
    private static final String CLAIM_NAME = "claim.name";
    private static final String ADD_TO_ID_TOKEN = "id.token.claim";
    private static final String TYPE = "jsonType.label";
    private static final String STRING = "String";
    private static final String USER_ATTR = "user.attribute";
    private static final String ADD_TO_USER_INFO = "userinfo.token.claim";
    private static final String GROUPS_NAME = "group";
    private static final String GROUP_MAPPER = "oidc-group-membership-mapper";
    private static final String ADD_FULL_PATH = "full.path";
    private static final String LOCALE_NAME = "locale";
    private static final String DIVISION_NAME = "division";

    private final List<ProtocolMapperRepresentation> target = new ArrayList<>();

    public void withFirstName() {
        var result = new ProtocolMapperRepresentation();
        result.setName(FIRST_NAME_NAME);
        result.setProtocol(OIDC);
        result.setProtocolMapper(MODEL_MAPPER);
        result.setConfig(Map.of(
                ADD_TO_ACCESS_TOKEN, "false",
                CLAIM_NAME, FIRST_NAME_NAME,
                ADD_TO_ID_TOKEN, "true",
                TYPE, STRING,
                USER_ATTR, FIRST_NAME_NAME,
                ADD_TO_USER_INFO, "true"
        ));

        target.add(result);
    }

    public void withLastName() {
        var result = new ProtocolMapperRepresentation();
        result.setName(LAST_NAME_NAME);
        result.setProtocol(OIDC);
        result.setProtocolMapper(MODEL_MAPPER);
        result.setConfig(Map.of(
                ADD_TO_ACCESS_TOKEN, "false",
                CLAIM_NAME, LAST_NAME_NAME,
                ADD_TO_ID_TOKEN, "true",
                TYPE, STRING,
                USER_ATTR, LAST_NAME_NAME,
                ADD_TO_USER_INFO, "true"
        ));

        target.add(result);
    }

    public void withMiddleName() {
        var result = new ProtocolMapperRepresentation();
        result.setName(MIDDLE_NAME_NAME);
        result.setProtocol(OIDC);
        result.setProtocolMapper(ATTR_MAPPER);
        result.setConfig(Map.of(
                ADD_TO_ACCESS_TOKEN, "true",
                CLAIM_NAME, MIDDLE_NAME_NAME,
                ADD_TO_ID_TOKEN, "true",
                TYPE, STRING,
                USER_ATTR, MIDDLE_NAME_NAME,
                ADD_TO_USER_INFO, "true"
        ));

        target.add(result);
    }

    public void withGroups() {
        var result = new ProtocolMapperRepresentation();
        result.setName(GROUPS_NAME);
        result.setProtocol(OIDC);
        result.setProtocolMapper(GROUP_MAPPER);
        result.setConfig(Map.of(
                ADD_TO_ACCESS_TOKEN, "true",
                CLAIM_NAME, GROUPS_NAME,
                ADD_TO_ID_TOKEN, "true",
                ADD_TO_USER_INFO, "true",
                ADD_FULL_PATH, "false"
        ));

        target.add(result);
    }

    public void withLocale() {
        var result = new ProtocolMapperRepresentation();
        result.setName(LOCALE_NAME);
        result.setProtocol(OIDC);
        result.setProtocolMapper(ATTR_MAPPER);
        result.setConfig(Map.of(
                ADD_TO_ACCESS_TOKEN, "false",
                CLAIM_NAME, LOCALE_NAME,
                ADD_TO_ID_TOKEN, "true",
                TYPE, STRING,
                USER_ATTR, LOCALE_NAME,
                ADD_TO_USER_INFO, "true"
        ));

        target.add(result);
    }

    public void withDivision() {
        var result = new ProtocolMapperRepresentation();
        result.setName(DIVISION_NAME);
        result.setProtocol(OIDC);
        result.setProtocolMapper(ATTR_MAPPER);
        result.setConfig(Map.of(
                ADD_TO_ACCESS_TOKEN, "true",
                CLAIM_NAME, DIVISION_NAME,
                ADD_TO_ID_TOKEN, "true",
                TYPE, STRING,
                USER_ATTR, DIVISION_NAME,
                ADD_TO_USER_INFO, "true"
        ));

        target.add(result);
    }

    public List<ProtocolMapperRepresentation> build() {
        return target;
    }
}

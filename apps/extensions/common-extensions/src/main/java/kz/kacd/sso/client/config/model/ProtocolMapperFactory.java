package kz.kacd.sso.client.config.model;

import kz.kacd.sso.v1.ClientSpec;
import org.keycloak.models.ProtocolMapperModel;

import java.util.HashMap;
import java.util.Map;

public class ProtocolMapperFactory {
    private static final String OIDC = "openid-connect";
    private static final String MODEL_MAPPER = "oidc-usermodel-property-mapper";
    private static final String ADD_TO_ACCESS_TOKEN = "access.token.claim";
    private static final String CLAIM_NAME = "claim.name";
    private static final String ADD_TO_ID_TOKEN = "id.token.claim";
    private static final String TYPE = "jsonType.label";
    private static final String STRING = "String";
    private static final String USER_ATTR = "user.attribute";
    private static final String ADD_TO_USER_INFO = "userinfo.token.claim";
    private static final String ATTR_MAPPER = "oidc-usermodel-attribute-mapper";
    private static final String GROUP_MAPPER = "oidc-group-membership-mapper";
    private static final String ADD_FULL_PATH = "full.path";
    private static final String ORG_MAPPER = "org-protocol-mapper";

    private ProtocolMapperFactory() {
    }

    public static ProtocolMapperModel create(ClientSpec.Attributes type) {
        switch (type) {
            case FIRST_NAME:
                return firstName();
            case LAST_NAME:
                return lastName();
            case MIDDLE_NAME:
                return middleName();
            case GROUPS:
                return groups();
            case LOCALE:
                return locale();
            case DIVISION:
                return division();
            case ORG:
                return org();
            case CLIENT_TYPE:
                return clientType();
            case IIN:
                return iin();
            case EMAIL_VERIFIED:
                return emailVerified();
            case RESIDENCY:
                return residency();
            case PHONE_NUMBER:
                return phoneNumber();
            case PHONE_NUMBER_VERIFIED:
                return phoneNumberVerified();
            case POSITION:
                return position();
            case EBR:
                return ebr();
        }
        return null;
    }

    private static ProtocolMapperModel ebr() {
        return attributeMapper("hasEBR", true);
    }

    private static ProtocolMapperModel firstName() {
        return modelMapper("firstName");
    }

    private static ProtocolMapperModel lastName() {
        return modelMapper("lastName");
    }

    private static ProtocolMapperModel emailVerified() {
        return modelMapper("emailVerified");
    }

    private static ProtocolMapperModel modelMapper(String field) {
        ProtocolMapperModel result = new ProtocolMapperModel();
        result.setName(field);
        result.setProtocol(OIDC);
        result.setProtocolMapper(MODEL_MAPPER);
        result.setConfig(modelMapperConfig(field));
        return result;
    }

    private static Map<String, String> modelMapperConfig(String field) {
        Map<String, String> result = new HashMap<>();
        result.put(ADD_TO_ACCESS_TOKEN, Boolean.FALSE.toString());
        result.put(CLAIM_NAME, field);
        result.put(ADD_TO_ID_TOKEN, Boolean.TRUE.toString());
        result.put(TYPE, STRING);
        result.put(USER_ATTR, field);
        result.put(ADD_TO_USER_INFO, Boolean.TRUE.toString());
        return result;
    }

    private static ProtocolMapperModel position() {
        return attributeMapper("position");
    }

    private static ProtocolMapperModel phoneNumber() {
        return attributeMapper("phoneNumber", true);
    }

    private static ProtocolMapperModel phoneNumberVerified() {
        return attributeMapper("phoneVerified", true);
    }

    private static ProtocolMapperModel residency() {
        return attributeMapper("residency", true);
    }

    private static ProtocolMapperModel middleName() {
        return attributeMapper("middleName");
    }

    private static ProtocolMapperModel locale() {
        return attributeMapper("locale");
    }

    private static ProtocolMapperModel division() {
        return attributeMapper("division", true);
    }

    private static ProtocolMapperModel clientType() {
        return attributeMapper("clientType", true);
    }

    private static ProtocolMapperModel iin() {
        return attributeMapper("iin");
    }

    private static ProtocolMapperModel attributeMapper(String attribute) {
        return attributeMapper(attribute, false);
    }

    private static ProtocolMapperModel attributeMapper(String attribute, boolean addToAccessToken) {
        ProtocolMapperModel result = new ProtocolMapperModel();
        result.setName(attribute);
        result.setProtocol(OIDC);
        result.setProtocolMapper(ATTR_MAPPER);
        result.setConfig(attributeMapperConfig(attribute, addToAccessToken));
        return result;
    }

    private static Map<String, String> attributeMapperConfig(String attribute, boolean addToAccessToken) {
        Map<String, String> result = new HashMap<>();
        result.put(ADD_TO_ACCESS_TOKEN, Boolean.toString(addToAccessToken));
        result.put(CLAIM_NAME, attribute);
        result.put(ADD_TO_ID_TOKEN, Boolean.TRUE.toString());
        result.put(TYPE, STRING);
        result.put(USER_ATTR, attribute);
        result.put(ADD_TO_USER_INFO, Boolean.TRUE.toString());
        return result;
    }

    private static ProtocolMapperModel groups() {
        return groupsMapper("group");
    }

    private static ProtocolMapperModel groupsMapper(String attribute) {
        ProtocolMapperModel result = new ProtocolMapperModel();
        result.setName(attribute);
        result.setProtocol(OIDC);
        result.setProtocolMapper(GROUP_MAPPER);
        result.setConfig(groupsMapperConfig(attribute));
        return result;
    }

    private static Map<String, String> groupsMapperConfig(String attribute) {
        Map<String, String> result = new HashMap<>();
        result.put(ADD_TO_ACCESS_TOKEN, Boolean.TRUE.toString());
        result.put(CLAIM_NAME, attribute);
        result.put(ADD_TO_ID_TOKEN, Boolean.TRUE.toString());
        result.put(ADD_TO_USER_INFO, Boolean.TRUE.toString());
        result.put(ADD_FULL_PATH, Boolean.FALSE.toString());
        return result;
    }

    private static ProtocolMapperModel org() {
        ProtocolMapperModel result = new ProtocolMapperModel();
        result.setName("org");
        result.setProtocol(OIDC);
        result.setProtocolMapper(ORG_MAPPER);
        result.setConfig(orgConfig());
        return result;
    }

    private static Map<String, String> orgConfig() {
        Map<String, String> result = new HashMap<>();
        result.put(ADD_TO_ACCESS_TOKEN, Boolean.FALSE.toString());
        result.put(ADD_TO_ID_TOKEN, Boolean.TRUE.toString());
        result.put(ADD_TO_USER_INFO, Boolean.TRUE.toString());
        return result;
    }
}

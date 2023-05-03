package kz.kacd.sso.external.bmg.mkb;

import kz.kacd.sso.external.bmg.MobilePhoneValidator;
import org.jboss.logging.Logger;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class MkbMobilePhoneValidator implements MobilePhoneValidator {
    private static final Logger log = Logger.getLogger(MkbMobilePhoneValidator.class);

    public static final String REALM_NAME = "internal";
    public static final String SCOPE_NAME = "mkb-bmg";
    public static final String CLIENT_ID = "internal-sso-api";
    private static final String FRONT_END_URL_ATTR = "frontendUrl";

    private final KeycloakSession session;
    private final KeycloakSelfClient selfClient;
    private final MkbHttpClient mkbHttpClient;

    public MkbMobilePhoneValidator(KeycloakSession session, KeycloakSelfClient selfClient, MkbHttpClient mkbHttpClient) {
        this.session = session;
        this.selfClient = selfClient;
        this.mkbHttpClient = mkbHttpClient;
    }

    @Override
    public boolean check(String iin, String phoneNumber) {
        log.infof("Checking phone number in bmg %s for iin %s ...", phoneNumber, iin);
        try {
            return mkbHttpClient.checkPhone(iin, phoneNumber.replace("+", ""), getAccessToken());
        } catch (Exception e) {
            log.warnf("Error on checking phone %s for iin %s!", phoneNumber, iin, e);
            return false;
        }
    }

    private String getAccessToken() {
        RealmModel realm = session.realms().getRealmByName(REALM_NAME);
        ClientModel client = realm.getClientByClientId(CLIENT_ID);

        String issuer = realm.getAttribute(FRONT_END_URL_ATTR) + "/realms/" + realm.getName();
        String tokenUrl = selfClient.getTokenUrl(issuer);
        return selfClient.getToken(tokenUrl, client.getClientId(), client.getSecret(), SCOPE_NAME);
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

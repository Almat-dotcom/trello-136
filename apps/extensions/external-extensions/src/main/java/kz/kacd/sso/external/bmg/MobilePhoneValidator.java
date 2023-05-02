package kz.kacd.sso.external.bmg;

import org.keycloak.provider.Provider;

public interface MobilePhoneValidator extends Provider {

    boolean check(String iin, String phoneNumber);
}

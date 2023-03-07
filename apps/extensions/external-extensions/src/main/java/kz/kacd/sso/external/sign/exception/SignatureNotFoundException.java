package kz.kacd.sso.external.sign.exception;

import kz.kacd.sso.external.model.page.ExternalMessages;

public class SignatureNotFoundException extends RuntimeException {

    public SignatureNotFoundException() {
        super(ExternalMessages.INVALID_EDS);
    }

}

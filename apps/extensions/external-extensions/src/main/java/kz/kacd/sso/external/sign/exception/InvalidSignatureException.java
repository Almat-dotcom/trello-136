package kz.kacd.sso.external.sign.exception;

import kz.kacd.sso.external.model.page.ExternalMessages;

public class InvalidSignatureException extends RuntimeException {

    public InvalidSignatureException() {
        super(ExternalMessages.INVALID_EDS);
    }
}

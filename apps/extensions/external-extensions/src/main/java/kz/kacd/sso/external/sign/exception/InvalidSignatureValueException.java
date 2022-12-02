package kz.kacd.sso.external.sign.exception;

import kz.kacd.sso.external.model.page.ExternalMessages;

public class InvalidSignatureValueException extends RuntimeException {

    public InvalidSignatureValueException() {
        super(ExternalMessages.INVALID_EDS);
    }
}

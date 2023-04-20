package kz.kacd.sso.sign.exception;

import kz.kcsd.state.sso.model.ExtensionMessages;

public class InvalidSignatureException extends RuntimeException {

    public InvalidSignatureException() {
        super(ExtensionMessages.INVALID_EDS);
    }
}

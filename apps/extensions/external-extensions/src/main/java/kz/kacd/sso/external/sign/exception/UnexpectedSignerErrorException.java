package kz.kacd.sso.external.sign.exception;

import org.keycloak.events.Errors;

public class UnexpectedSignerErrorException extends RuntimeException {
    public UnexpectedSignerErrorException() {
        super(Errors.GENERIC_AUTHENTICATION_ERROR);
    }
}

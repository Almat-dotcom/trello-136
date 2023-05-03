package kz.kacd.sso.external.bmg.exception;

public class UnexpectedResponseStatusException extends MobileVerificationException {
    public UnexpectedResponseStatusException(int code) {
        super("Unexpected response status code: " + code + "!");
    }
}

package kz.kacd.sso.external.model.jpa.exception;

import java.util.List;

public class InvalidListOfScopesException extends RuntimeException {
    public InvalidListOfScopesException(List<String> scopes) {
        super("Invalid list of scopes provided: " + scopes + "!");
    }
}

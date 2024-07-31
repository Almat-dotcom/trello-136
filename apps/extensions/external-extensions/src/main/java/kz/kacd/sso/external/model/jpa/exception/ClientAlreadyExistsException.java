package kz.kacd.sso.external.model.jpa.exception;

public class ClientAlreadyExistsException extends RuntimeException {
    public ClientAlreadyExistsException(String clientId) {
        super("Client id " + clientId + " has already been registered!");
    }
}

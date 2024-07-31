package kz.kacd.sso.external.model.jpa.exception;

public class ClientNotFoundException extends RuntimeException{
    public ClientNotFoundException(String bin, String clientId) {
        super("Client by clientId " + clientId + " for org " + bin + " not found!");
    }
}

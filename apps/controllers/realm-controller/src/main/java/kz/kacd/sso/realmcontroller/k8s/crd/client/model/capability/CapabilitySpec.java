package kz.kacd.sso.realmcontroller.k8s.crd.client.model.capability;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class CapabilitySpec {


    @JsonPropertyDescription(
            "Type of the client. By default it is PUBLIC. PUBLIC means that client has no client secret and " +
                    "can authenticate users only using standard OAuth2.0 flow. CONFIDENTIAL means that the client " +
                    "has ability to protect it's secret data, so it can authenticate users using password access grand " +
                    "and can issue access token using it's own credentials. BEARER_ONLY means that this is resource server " +
                    "which can't authenticate users at all, so, it only can receive bearer tokens."
    )
    private Type type;

    public enum Type {
        /**
         * Client has no client secret.
         */
        PUBLIC,
        /**
         * Client has client secret and can authenticate users by their passwords, and can receive access tokens by
         * its own credentials.
         */
        CONFIDENTIAL,
        /**
         * Client is a resource server with only bearer access control.
         */
        BEARER_ONLY
    }
}

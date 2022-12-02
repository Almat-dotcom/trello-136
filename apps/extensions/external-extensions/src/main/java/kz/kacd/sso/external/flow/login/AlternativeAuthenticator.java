package kz.kacd.sso.external.flow.login;

import org.keycloak.authentication.AuthenticationFlowContext;

public interface AlternativeAuthenticator {

    /**
     * Determines if current authenticator is configured for this context.
     * </>
     * IMPORTANT! Do not change anything in context! You can only read of it!
     * </>
     * Returns true if this context fits alternative authenticator needs.
     * If it returns false another authenticator will be used.
     */
    boolean isConfiguredFor(AuthenticationFlowContext context);

    /**
     * Called form action.
     * </>
     * You should fill login event and context properly.
     */
    void action(AuthenticationFlowContext context);
}

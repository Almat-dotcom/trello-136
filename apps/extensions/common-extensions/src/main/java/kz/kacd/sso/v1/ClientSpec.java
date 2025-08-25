package kz.kacd.sso.v1;

public interface ClientSpec {
    String getClientId();
    boolean isEnabled();
    String[] getRedirectUris();
    String[] getWebOrigins();
    
    // Display settings
    String getDisplayedName();
    String getDisplayedDescription();
    
    // Client configuration settings
    String getProtocol();
    String getClientAuthenticatorType();
    Boolean getFullScopeAllowed();
    Boolean getStandardFlowEnabled();
    Boolean getServiceAccountsEnabled();
    Boolean getDirectAccessGrantsEnabled();
    Boolean getImplicitFlowEnabled();
    
    // Capability settings
    String getCapabilityType(); // PUBLIC, CONFIDENTIAL, BEARER_ONLY
    String getClientExistingSecret(); // Kubernetes secret name
    String getClientSecretKey(); // Key in the Kubernetes secret
    
    // Attributes
    String[] getAttributes(); // Client attributes like FIRST_NAME, LAST_NAME, etc.
    
    // Roles
    RoleSpec[] getRoles(); // Client roles
}


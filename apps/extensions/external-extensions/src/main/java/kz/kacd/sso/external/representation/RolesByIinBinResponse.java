package kz.kacd.sso.external.representation;

import java.util.List;
import java.util.Map;

public class RolesByIinBinResponse {
    private String iin;
    private String email;
    private String firstName;
    private String lastName;
    private Map<String, List<String>> roles;

    public String getIin() { return iin; }
    public void setIin(String iin) { this.iin = iin; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Map<String, List<String>> getRoles() { return roles; }
    public void setRoles(Map<String, List<String>> roles) { this.roles = roles; }
} 
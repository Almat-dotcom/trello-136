package kz.kacd.sso.external.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.keycloak.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResourceRepresentation {

    private final String id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final String iin;
    private final Boolean enabled;
    private final Map<String, List<String>> roles;
    private final List<String> groups;

    public ProfileResourceRepresentation(
            String id,
            String username,
            String email,
            String firstName,
            String lastName,
            String middleName,
            String iin,
            Boolean enabled,
            Map<String, List<String>> roles,
            List<String> groups
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.iin = iin;
        this.enabled = enabled;
        this.roles = roles;
        this.groups = groups;
    }

    public static ProfileResourceRepresentation of(KeycloakSession session, RealmModel realm, UserModel user) {
        String id = user.getId();
        String username = user.getUsername();
        String email = user.getEmail();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String middleName = user.getFirstAttribute("middleName");
        String iin = user.getFirstAttribute("iin");
        Boolean enabled = user.isEnabled();
        Map<String, List<RoleModel>> sourceRoles = user.getRoleMappingsStream()
                .collect(Collectors.groupingBy(RoleModel::getContainerId));
        Map<String, List<String>> roles = new HashMap<>();
        sourceRoles.forEach((k, v) -> {
            List<String> names = v.stream().map(RoleModel::getName).collect(Collectors.toList());
            ClientModel client = session.clients().getClientById(realm, k);
            if (client == null && roles.containsKey("realm")) {
                roles.get("realm").addAll(names);
            } else if (client == null) {
                roles.put("realm", names);
            } else {
                roles.put(client.getClientId(), names);
            }
        });
        List<String> groups = user.getGroupsStream().map(GroupModel::getName).collect(Collectors.toList());
        return new ProfileResourceRepresentation(
                id,
                username,
                email,
                firstName,
                lastName,
                middleName,
                iin,
                enabled,
                roles,
                groups
        );
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getIin() {
        return iin;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Map<String, List<String>> getRoles() {
        return roles;
    }

    public List<String> getGroups() {
        return groups;
    }
}

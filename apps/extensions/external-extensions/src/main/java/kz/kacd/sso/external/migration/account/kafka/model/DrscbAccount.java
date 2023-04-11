package kz.kacd.sso.external.migration.account.kafka.model;

import java.time.LocalDateTime;
import java.util.List;

public class DrscbAccount {

    public static final String LK_CLIENT = "lk-shell-front";
    public static final String LEGACY_CLIENT = "kcsd-legacy-portal";
    public static final String MOCK_EMAIL = "example.com";

    private final String id;
    private final Kind kind;
    private final boolean resident;
    private final String bin;
    private final String iin;
    private final String legalName;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final String email;
    private final String phoneNumber;
    private final LocalDateTime registeredAt;
    private final LocalDateTime endedAt;
    private final boolean enabled;
    private final List<Role> roles;

    DrscbAccount(
            String id,
            Kind kind,
            boolean resident,
            String bin,
            String iin,
            String legalName,
            String firstName,
            String lastName,
            String middleName,
            String email,
            String phoneNumber,
            LocalDateTime registeredAt,
            LocalDateTime endedAt,
            boolean enabled,
            List<Role> roles) {
        this.id = id;
        this.kind = kind;
        this.resident = resident;
        this.bin = bin;
        this.iin = iin;
        this.legalName = legalName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.registeredAt = registeredAt;
        this.endedAt = endedAt;
        this.enabled = enabled;
        this.roles = roles;
    }

    public boolean emailPresent() {
        return !email.endsWith(MOCK_EMAIL);
    }

    public boolean phoneNumberPresent() {
        return phoneNumber != null;
    }

    public boolean namesPresent() {
        return firstName != null || lastName != null || middleName != null;
    }

    public String getId() {
        return id;
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isResident() {
        return resident;
    }

    public String getBin() {
        return bin;
    }

    public String getIin() {
        return iin;
    }

    public String getLegalName() {
        return legalName;
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

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public enum Kind {
        PERSONAL_ACCOUNT,
        LEGAL_ACCOUNT,
        LEGAL_EMPLOYEE_ACCOUNT
    }

    public enum Role {

        DEPO("depositor", LK_CLIENT),
        ISSUER("issuer", LK_CLIENT),
        HOLDER("shareholder", LK_CLIENT),
        DOCKACD("dockacd", LEGACY_CLIENT),
        INFODOC("infodoc", LEGACY_CLIENT),
        NEWS("news", LEGACY_CLIENT),
        PFI("pfi", LEGACY_CLIENT);

        private final String name;
        private final String client;

        Role(String name, String client) {
            this.name = name;
            this.client = client;
        }

        public String roleName() {
            return name;
        }

        public String clientId() {
            return client;
        }
    }
}

package kz.kacd.sso.external.migration.account.kafka.model;

import kz.kacd.sso.external.migration.account.kafka.representation.DrscbPersonRepresentation;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DrscbAccountFactory {

    private DrscbAccountFactory() {
    }

    public static DrscbAccount create(DrscbPersonRepresentation representation) {
        String id = idOf(representation);
        DrscbAccount.Kind kind = kindOf(representation);
        boolean resident = residentOf(representation);
        String email = emailOf(representation);
        String phoneNumber = phoneNumberOf(representation);
        List<DrscbAccount.Role> roles = rolesOf(representation);

        return new DrscbAccount(
                id,
                kind,
                resident,
                representation.getBin(),
                representation.getIin(),
                representation.getRusName(),
                representation.getFirstNameRus(),
                representation.getLastNameRus(),
                representation.getMidNameRus(),
                email,
                phoneNumber,
                ofEpochMillis(representation.getRegDate()),
                ofEpochMillis(representation.getDateEnd()),
                representation.getDateEnd() == null,
                roles
        );
    }

    private static String idOf(DrscbPersonRepresentation representation) {
        return representation.getId().toString();
    }

    private static DrscbAccount.Kind kindOf(DrscbPersonRepresentation representation) {
        if (representation.getBin() != null && representation.getIin() == null) {
            return DrscbAccount.Kind.LEGAL_ACCOUNT;
        } else if (representation.getBin() != null) {
            return DrscbAccount.Kind.LEGAL_EMPLOYEE_ACCOUNT;
        } else {
            return DrscbAccount.Kind.PERSONAL_ACCOUNT;
        }
    }

    private static boolean residentOf(DrscbPersonRepresentation representation) {
        return representation.getIrs().equals("1");
    }

    private static String emailOf(DrscbPersonRepresentation representation) {
        return representation.getEmail() != null
                ? representation.getEmail()
                : representation.getId() + "@" + DrscbAccount.MOCK_EMAIL;
    }

    private static String phoneNumberOf(DrscbPersonRepresentation representation) {
        if (representation.getmPhone() != null) {
            return formatPhone(representation.getmPhone());
        }
        if (representation.getTel() != null) {
            return formatPhone(representation.getTel());
        }
        if (representation.getPhone() != null) {
            return formatPhone(representation.getPhone());
        }
        return null;
    }

    private static String formatPhone(String source) {
        return source.replace("-", "").replace(" ", "")
                .replace("(", "")
                .replace(")", "");
    }

    private static List<DrscbAccount.Role> rolesOf(DrscbPersonRepresentation representation) {
        if (representation.getRights() == null) {
            return Collections.emptyList();
        }
        return representation.getRights().stream()
                .map(it -> DrscbAccount.Role.valueOf(it.getName()))
                .collect(Collectors.toList());
    }

    private static LocalDateTime ofEpochMillis(Long source) {
        if (source == null) {
            return null;
        }

        long seconds = source / 1_000;
        long nanos = (source % 1_000) * 1_000_000;
        return LocalDateTime.ofEpochSecond(seconds, (int) nanos, ZoneOffset.ofHours(6));
    }
}

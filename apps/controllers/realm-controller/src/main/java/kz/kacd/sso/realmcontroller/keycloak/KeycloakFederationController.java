package kz.kacd.sso.realmcontroller.keycloak;

import kz.kacd.sso.realmcontroller.k8s.crd.model.federation.FederationSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.federation.ldap.LdapGroupsMappingSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.federation.ldap.LdapSearchingSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.federation.ldap.LdapSpec;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import kz.kacd.sso.realmcontroller.keycloak.model.LdapAttributeMapperBuilder;
import kz.kacd.sso.realmcontroller.keycloak.model.LdapBuilder;
import kz.kacd.sso.realmcontroller.keycloak.model.LdapGroupMapperBuilder;
import kz.kacd.sso.realmcontroller.keycloak.model.Realm;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakFederationController {
    private static final String MIDDLE_NAME = "middle name";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";

    private final KeycloakComponentRepository repository;

    public OperationResponse<Boolean> apply(Realm realm, FederationSpec spec, Map<String, SecretData> secrets) {
        if (spec == null) {
            return OperationResponse.success(true);
        }

        return saveLdap(realm, spec.getLdap(), secrets);
    }

    private OperationResponse<Boolean> saveLdap(Realm realm, LdapSpec spec, Map<String, SecretData> secrets) {
        if (spec == null) {
            return OperationResponse.success(true);
        }

        var ldap = saveBasicLdapComponent(realm, spec, secrets);
        if (ldap instanceof OperationResponse.Success<ComponentRepresentation> s) {
            return saveAdditionalLdapComponents(realm, s.getData(), spec);
        } else {
            return ldap.map(it -> true);
        }
    }

    private OperationResponse<ComponentRepresentation> saveBasicLdapComponent(
            Realm realm,
            LdapSpec spec,
            Map<String, SecretData> secrets
    ) {
        var target = new LdapBuilder(realm.representation().getId()).buildFrom(spec, secrets);
        return repository.save(realm, target);
    }

    private OperationResponse<Boolean> saveAdditionalLdapComponents(
            Realm realm,
            ComponentRepresentation parent,
            LdapSpec spec
    ) {
        var middleName = saveLdapMiddleName(realm, parent, spec.getSearching().getMode());
        if (middleName instanceof OperationResponse.Failure) {
            return middleName;
        }
        var firstName = saveLdapFirstName(realm, parent, spec.getSearching().getMode());
        if (firstName instanceof OperationResponse.Failure) {
            return firstName;
        }
        var lastName = saveLdapLastName(realm, parent, spec.getSearching().getMode());
        if (lastName instanceof OperationResponse.Failure) {
            return lastName;
        }
        var group = saveLdapGroup(realm, parent, spec.getGroups());
        if (group instanceof OperationResponse.Failure) {
            return group;
        }
        return OperationResponse.success(true);
    }

    private OperationResponse<Boolean> saveLdapMiddleName(
            Realm realm,
            ComponentRepresentation parent,
            LdapSearchingSpec.Modes mode
    ) {
        return repository.save(
                realm,
                new LdapAttributeMapperBuilder(parent)
                        .withName(MIDDLE_NAME)
                        .withAttributeMapping(
                                "middleName",
                                "middleName",
                                mode == LdapSearchingSpec.Modes.R,
                                false
                        )
                        .build()
        ).map(it -> true);
    }

    private OperationResponse<Boolean> saveLdapFirstName(
            Realm realm,
            ComponentRepresentation parent,
            LdapSearchingSpec.Modes mode
    ) {
        return repository.save(
                realm,
                new LdapAttributeMapperBuilder(parent)
                        .withName(FIRST_NAME)
                        .withAttributeMapping(
                                "givenName",
                                "firstName",
                                mode == LdapSearchingSpec.Modes.R,
                                true
                        )
                        .build()
        ).map(it -> true);
    }

    private OperationResponse<Boolean> saveLdapLastName(
            Realm realm,
            ComponentRepresentation parent,
            LdapSearchingSpec.Modes mode
    ) {
        return repository.save(
                realm,
                new LdapAttributeMapperBuilder(parent)
                        .withName(LAST_NAME)
                        .withAttributeMapping(
                                "sn",
                                "lastName",
                                mode == LdapSearchingSpec.Modes.R,
                                true
                        )
                        .build()
        ).map(it -> true);
    }

    private OperationResponse<Boolean> saveLdapGroup(
            Realm realm,
            ComponentRepresentation parent,
            LdapGroupsMappingSpec spec
    ) {
        return repository.save(
                realm,
                new LdapGroupMapperBuilder(parent)
                        .withSpec(spec)
                        .build()
        ).map(it -> true);
    }
}

package kz.kacd.sso.realmcontroller.keycloak.component;

import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.FederationSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.ldap.LdapGroupsMappingSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.ldap.LdapSearchingSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.model.federation.ldap.LdapSpec;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import kz.kacd.sso.realmcontroller.keycloak.Realm;
import kz.kacd.sso.realmcontroller.keycloak.component.model.*;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakFederationController {
    private static final String MIDDLE_NAME = "middle name";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String DIVISION = "division";

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
        var division = saveDivision(realm, parent);
        if (division instanceof OperationResponse.Failure) {
            return division;
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

    private OperationResponse<Boolean> saveDivision(
            Realm realm,
            ComponentRepresentation parent
    ) {
        return repository.save(
                realm,
                new LdapDivisionMapperBuilder(parent)
                        .withName(DIVISION)
                        .withAttributeMapping("distinguishedName", "division")
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


    public OperationResponse<Boolean> importGroups(Realm realm) {
        log.debug("Importing groups into keycloak ...");
        var ldap = repository.find(realm, LdapBuilder.LDAP_PROVIDER_ID);
        if (ldap instanceof OperationResponse.Failure<ComponentRepresentation>) {
            return ldap.map(it -> true);
        }
        var groups = repository.find(
                realm,
                ((OperationResponse.Success<ComponentRepresentation>) ldap).getData().getId(),
                LdapGroupMapperBuilder.PROVIDER_ID
        );
        if (groups instanceof OperationResponse.Failure<ComponentRepresentation>) {
            return groups.map(it -> true);
        }

        return repository.sync(realm, ((OperationResponse.Success<ComponentRepresentation>) groups).getData(), true);
    }

    public OperationResponse<ComponentRepresentation> getLdap(Realm realm) {
        return repository.find(realm, LdapBuilder.LDAP_PROVIDER_ID);
    }

    public OperationResponse<ComponentRepresentation> addLdapRoleMapper(
            Realm realm,
            String parentId,
            String clientId,
            String dn
    ) {
        log.debug("Adding ldap role mapper to realm {} and clientId {} ...", realm.name(), clientId);
        return repository.save(
                realm,
                new LdapRoleMapperBuilder(parentId)
                        .withClientAndDn(clientId, dn)
                        .build()
        );
    }

    public OperationResponse<Boolean> deleteLdapRoleMapper(
            Realm realm,
            String client
    ) {
        var res = repository.findAll(realm, LdapRoleMapperBuilder.PROVIDER_ID);
        if (res instanceof OperationResponse.Failure<List<ComponentRepresentation>>) {
            return res.map(it -> true);
        }
        var mapper = ((OperationResponse.Success<List<ComponentRepresentation>>) res)
                .getData()
                .stream()
                .filter(it -> it.getName().startsWith(client))
                .findFirst();
        if (mapper.isEmpty()) {
            return OperationResponse.notFound("Mapper for client " + client + " in realm " + realm.name() + " not found!");
        }
        return repository.delete(realm, mapper.get().getId());
    }

    public OperationResponse<Boolean> sync(Realm realm, ComponentRepresentation component, boolean reversed) {
        log.debug("Syncing for components {} of realm {} ...", component.getName(), realm.name());
        return repository.sync(realm, component, reversed);
    }
}

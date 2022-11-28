package kz.kacd.sso.external.model;

import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.Provider;

import java.util.stream.Stream;

public interface OrganizationProvider extends Provider {

    OrganizationModel createOrganization(RealmModel model, UserModel createdBy);

    OrganizationModel getOrganizationById(RealmModel model, String id);

    OrganizationModel getOrganizationByBin(RealmModel model, String bin);

    Stream<OrganizationModel> getUserOrganizations(RealmModel realm, UserModel user);

    Stream<OrganizationModel> getOrganizations(RealmModel realm, Integer firstResult, Integer maxResult);

    default Stream<OrganizationModel> getOrganizations(RealmModel realm) {
        return getOrganizations(realm, null, null);
    }

    default void removeOrganizations(RealmModel realm) {
        getOrganizations(realm).forEach(it -> removeOrganization(realm, it.getId()));
    }

    boolean removeOrganization(RealmModel realm, String id);
}

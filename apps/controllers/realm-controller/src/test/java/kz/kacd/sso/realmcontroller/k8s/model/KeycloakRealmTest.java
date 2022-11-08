package kz.kacd.sso.realmcontroller.k8s.model;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import kz.kacd.sso.realmcontroller.k8s.crd.Realm;
import kz.kacd.sso.realmcontroller.k8s.crd.model.RealmSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.model.RealmStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KeycloakRealmTest {

    @Test
    void should_change_state_to_detected() {
        var realm = new KeycloakRealm(testSource(), null, KeycloakRealm.RealmAction.ADDED);

        var actual = realm.detected();

        assertThat(actual.status().getState()).isEqualTo(RealmStatus.RealmState.DETECTED);
    }

    @Test
    void should_reject_detected_state_change_if_status_is_not_null() {
        var source = testSource();
        source.setStatus(RealmStatus.builder().state(RealmStatus.RealmState.APPLIED).build());

        var realm = new KeycloakRealm(source, null, KeycloakRealm.RealmAction.ADDED);

        assertThrows(
                IllegalStateException.class,
                realm::detected
        );
    }

    @Test
    void should_change_state_to_applying() {
        var realm = new KeycloakRealm(testSource(), null, KeycloakRealm.RealmAction.ADDED)
                .detected();

        var actual = realm.applying();

        assertThat(actual.status().getState()).isEqualTo(RealmStatus.RealmState.APPLYING);
    }

    @Test
    void should_reject_applying_state_change_if_realm_is_not_detected() {
        var realm = new KeycloakRealm(testSource(), null, KeycloakRealm.RealmAction.ADDED);

        assertThrows(
                IllegalStateException.class,
                realm::applying
        );
    }

    @Test
    void should_change_state_to_applied() {
        var realm = new KeycloakRealm(testSource(), null, KeycloakRealm.RealmAction.ADDED)
                .detected()
                .applying();

        var actual = realm.applied();

        assertThat(actual.status().getState()).isEqualTo(RealmStatus.RealmState.APPLIED);
    }

    @Test
    void should_reject_applied_state_change_if_realm_is_not_in_applying_state() {
        var realm = new KeycloakRealm(testSource(), null, KeycloakRealm.RealmAction.ADDED)
                .detected();

        assertThrows(
                IllegalStateException.class,
                realm::applied
        );
    }

    @Test
    void should_change_state_to_failed() {
        var realm = new KeycloakRealm(testSource(), null, KeycloakRealm.RealmAction.ADDED)
                .detected()
                .applying();

        var actual = realm.failed(new RuntimeException("Test error!"));

        assertThat(actual.status().getState()).isEqualTo(RealmStatus.RealmState.FAILED);
    }

    @Test
    void should_reject_failed_state_change_if_realm_is_not_in_applying_state() {
        var realm = new KeycloakRealm(testSource(), null, KeycloakRealm.RealmAction.ADDED)
                .detected();

        assertThrows(
                IllegalStateException.class,
                () -> realm.failed("Test error")
        );
    }

    Realm testSource() {
        var result = new Realm();
        result.setMetadata(
                new ObjectMetaBuilder()
                        .withName("test")
                        .withNamespace("test_ns")
                        .build()
        );
        result.setKind("Realm");
        result.setSpec(RealmSpec.builder().build());
        return result;
    }
}
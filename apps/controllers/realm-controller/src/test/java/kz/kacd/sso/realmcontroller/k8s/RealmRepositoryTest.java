package kz.kacd.sso.realmcontroller.k8s;

import io.fabric8.kubernetes.api.model.ListMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import kz.kacd.sso.realmcontroller.k8s.crd.Realm;
import kz.kacd.sso.realmcontroller.k8s.crd.RealmList;
import kz.kacd.sso.realmcontroller.k8s.crd.model.RealmStatus;
import kz.kacd.sso.realmcontroller.k8s.model.K8sResponse;
import kz.kacd.sso.realmcontroller.k8s.model.KeycloakRealm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.UnaryOperator;

import static kz.kacd.sso.realmcontroller.k8s.TestClientUtils.mockRealmEdit;
import static kz.kacd.sso.realmcontroller.k8s.TestClientUtils.mockRealmListResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RealmRepositoryTest {

    @Mock
    K8sClientFactory factory;
    @Mock
    KubernetesClient client;
    RealmRepository realmRepository;

    @BeforeEach
    void setUp() {
        given(factory.create()).willReturn(client);
        realmRepository = new RealmRepository("test", factory);
    }

    @Test
    void should_return_success() {
        var response = new RealmList();
        response.setMetadata(new ListMeta());
        response.setItems(List.of(mock(Realm.class)));
        mockRealmListResource(client, () -> response);

        var actual = realmRepository.list();

        assertThat(actual).isInstanceOf(K8sResponse.Success.class);
    }

    @Test
    void should_return_not_found_if_list_is_empty() {
        var response = new RealmList();
        response.setMetadata(new ListMeta());
        response.setItems(List.of());
        mockRealmListResource(client, () -> response);

        var actual = realmRepository.list();

        assertThat(actual).isInstanceOf(K8sResponse.Failure.class);
        var error = (K8sResponse.Failure<?>) actual;
        assertThat(error.getKind()).isEqualTo(K8sResponse.K8sFailures.NOT_FOUND);
    }

    @Test
    void should_return_internal_error() {
        mockRealmListResource(client, () -> {
            throw new RuntimeException("Test error!");
        });

        var actual = realmRepository.list();

        assertThat(actual).isInstanceOf(K8sResponse.Failure.class);
        var error = (K8sResponse.Failure<?>) actual;
        assertThat(error.getKind()).isEqualTo(K8sResponse.K8sFailures.INTERNAL_ERROR);
    }

    @Test
    void should_edit_realm() {
        var source = new Realm();
        source.setMetadata(new ObjectMetaBuilder().withName("test").withNamespace("test").build());
        var realm = new KeycloakRealm(
                source,
                RealmStatus.builder().state(RealmStatus.RealmState.APPLIED).build(),
                KeycloakRealm.RealmAction.ADDED
        );
        var res = mockRealmEdit(client, realm.source(), r -> r);

        var actual = realmRepository.save(realm);

        assertThat(actual).isInstanceOf(K8sResponse.Success.class);
        then(res).should().patchStatus();
    }
}
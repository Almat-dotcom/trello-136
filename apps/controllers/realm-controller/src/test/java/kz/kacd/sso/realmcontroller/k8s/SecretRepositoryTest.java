package kz.kacd.sso.realmcontroller.k8s;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import kz.kacd.sso.realmcontroller.model.OperationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kz.kacd.sso.realmcontroller.k8s.TestClientUtils.mockSecretResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SecretRepositoryTest {

    @Mock
    K8sClientFactory factory;
    @Mock
    KubernetesClient client;
    SecretRepository secretRepository;

    @BeforeEach
    void setUp() {
        given(factory.create()).willReturn(client);
        secretRepository = new SecretRepository("test", factory);
    }

    @Test
    void should_return_success() {
        var name = "test";
        mockSecretResource(client, name, () -> mock(Secret.class));

        var actual = secretRepository.find(name);

        assertThat(actual).isInstanceOf(OperationResponse.Success.class);
    }

    @Test
    void should_return_not_found() {
        var name = "test";
        mockSecretResource(client, name, () -> null);

        var actual = secretRepository.find(name);

        assertThat(actual).isInstanceOf(OperationResponse.Failure.class);
        var error = (OperationResponse.Failure<?>) actual;
        assertThat(error.getKind()).isEqualTo(OperationResponse.K8sFailures.NOT_FOUND);
    }

    @Test
    void should_return_error() {
        var name = "test";
        mockSecretResource(client, name, () -> {
            throw new RuntimeException("Test error!");
        });

        var actual = secretRepository.find(name);

        assertThat(actual).isInstanceOf(OperationResponse.Failure.class);
        var error = (OperationResponse.Failure<?>) actual;
        assertThat(error.getKind()).isEqualTo(OperationResponse.K8sFailures.INTERNAL_ERROR);
    }
}
//package kz.kacd.sso.k8s.realm;
//
//import io.fabric8.kubernetes.api.model.ObjectMeta;
//import kz.kacd.sso.k8s.realm.repository.K8sRealmRepository;
//import kz.kacd.sso.v1.Realm;
//import kz.kacd.sso.v1.RealmSpec;
//import kz.kacd.sso.v1.RealmStatus;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.keycloak.models.KeycloakSession;
//import org.keycloak.models.KeycloakSessionFactory;
//import org.keycloak.provider.ProviderEvent;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//import static org.mockito.Mockito.doNothing;
//
//@ExtendWith(MockitoExtension.class)
//class K8sRealmAdapterTest {
//
//    @Mock
//    KeycloakSessionFactory sessionFactory;
//    @Mock
//    KeycloakSession session;
//    @Mock
//    K8sRealmRepository repository;
//
//    K8sRealmAdapter adapter;
//
//    @BeforeEach
//    void setUp() {
//        given(session.getKeycloakSessionFactory()).willReturn(sessionFactory);
//
//        adapter = new K8sRealmAdapter(session, repository, testRealm());
//    }
//
//    @Test
//    void should_change_state_to_applying() {
//        ArgumentCaptor<ProviderEvent> event = ArgumentCaptor.forClass(ProviderEvent.class);
//        doNothing().when(sessionFactory).publish(event.capture());
//
//        adapter.applying();
//        RealmStatus actual = adapter.getStatus();
//
//        assertThat(actual.getState()).isEqualTo(RealmStatus.State.APPLYING);
//        then(repository).should().updateStatus(adapter.getName(), actual);
//        assertThat(event.getValue()).isInstanceOf(K8sRealm.K8sRealmApplyingEvent.class);
//    }
//
//    @Test
//    void should_change_state_to_applied() {
//        ArgumentCaptor<ProviderEvent> event = ArgumentCaptor.forClass(ProviderEvent.class);
//        doNothing().when(sessionFactory).publish(event.capture());
//
//        adapter.applied();
//        RealmStatus actual = adapter.getStatus();
//
//        assertThat(actual.getState()).isEqualTo(RealmStatus.State.APPLIED);
//        then(repository).should().updateStatus(adapter.getName(), actual);
//        assertThat(event.getValue()).isInstanceOf(K8sRealm.K8sRealmAppliedEvent.class);
//    }
//
//    @Test
//    void should_change_state_to_backoff() {
//        ArgumentCaptor<ProviderEvent> event = ArgumentCaptor.forClass(ProviderEvent.class);
//        doNothing().when(sessionFactory).publish(event.capture());
//
//        adapter.toBackOff(new RuntimeException("Test"));
//        RealmStatus actual = adapter.getStatus();
//
//        assertThat(actual.getState()).isEqualTo(RealmStatus.State.BACK_OFF);
//        then(repository).should().updateStatus(adapter.getName(), actual);
//        assertThat(event.getValue()).isInstanceOf(K8sRealm.K8sRealmBackOffedEvent.class);
//    }
//
//    @Test
//    void should_change_state_to_failed() {
//        ArgumentCaptor<ProviderEvent> event = ArgumentCaptor.forClass(ProviderEvent.class);
//        doNothing().when(sessionFactory).publish(event.capture());
//
//        adapter.failed(new RuntimeException("Test"));
//        RealmStatus actual = adapter.getStatus();
//
//        assertThat(actual.getState()).isEqualTo(RealmStatus.State.FAILED);
//        then(repository).should().updateStatus(adapter.getName(), actual);
//        assertThat(event.getValue()).isInstanceOf(K8sRealm.K8sRealmFailedEvent.class);
//    }
//
//    private Realm testRealm() {
//        ObjectMeta meta = new ObjectMeta();
//        meta.setName("test");
//        meta.setGeneration(1L);
//
//        Realm result = new Realm();
//        result.setMetadata(meta);
//        result.setSpec(new RealmSpec());
//        result.setStatus(null);
//
//        return result;
//    }
//
//}
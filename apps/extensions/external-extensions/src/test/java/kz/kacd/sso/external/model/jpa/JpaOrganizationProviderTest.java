package kz.kacd.sso.external.model.jpa;

import jakarta.persistence.EntityManager;
import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.jpa.entity.OrganizationEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class JpaOrganizationProviderTest {

    @Mock
    EntityManager em;
    @Mock
    KeycloakSessionFactory sessionFactory;
    @Mock
    KeycloakSession session;
    @Mock
    AdaptersFactory adapterFactory;
    @Mock
    OrganizationAdapter adapter;
    @Mock
    RealmModel realm;
    @Mock
    UserModel user;
    @InjectMocks
    JpaOrganizationProvider provider;

    @Test
    void should_persist_new_organization() {
        given(realm.getId()).willReturn("test");
        given(user.getId()).willReturn("test");
        given(session.getKeycloakSessionFactory()).willReturn(sessionFactory);
        given(adapterFactory.create(any(), any(), any(), any(), any())).willReturn(adapter);

        provider.createOrganization(realm, user);

        then(em).should().persist(any());
    }

    @Test
    void should_publish_org_created_event() {
        given(realm.getId()).willReturn("test");
        given(user.getId()).willReturn("test");
        given(session.getKeycloakSessionFactory()).willReturn(sessionFactory);
        given(adapterFactory.create(any(), any(), any(), any(), any())).willReturn(adapter);

        provider.createOrganization(realm, user);

        ArgumentCaptor<ProviderEvent> captor = ArgumentCaptor.forClass(ProviderEvent.class);
        then(sessionFactory).should().publish(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(OrganizationModel.OrganizationCreatedEvent.class);
    }

    @Test
    void should_add_created_by_as_confirmed_position_of_head() {
        given(realm.getId()).willReturn("test");
        given(user.getId()).willReturn("test");
        given(session.getKeycloakSessionFactory()).willReturn(sessionFactory);
        given(adapterFactory.create(any(), any(), any(), any(), any())).willReturn(adapter);

        provider.createOrganization(realm, user);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        then(adapter).should().requestPosition(captor.capture(), any());
        assertThat(captor.getValue()).isEqualTo(PositionModel.HEAD);
        then(adapter).should().confirmPosition(any());
    }

    @Test
    void should_return_organization_by_id() {
        String id = "my-id";
        String realmId = "test";
        given(realm.getId()).willReturn(realmId);
        given(em.find(OrganizationEntity.class, id))
                .willReturn(new OrganizationEntity(id, realmId, "test"));
        given(adapterFactory.create(any(), any(), any(), any(), any())).willReturn(adapter);

        OrganizationModel actual = provider.getOrganizationById(realm, id);

        assertThat(actual).isNotNull();
    }

    @Test
    void should_ignore_not_current_realm_organizations() {
        String id = "my-id";
        given(realm.getId()).willReturn("another");
        given(em.find(OrganizationEntity.class, id))
                .willReturn(new OrganizationEntity(id, "some", "test"));

        OrganizationModel actual = provider.getOrganizationById(realm, id);

        assertThat(actual).isNull();
    }

    @Test
    void should_ignore_removing_if_org_does_not_exist() {
        String id = "my-id";
        given(em.find(OrganizationEntity.class, id)).willReturn(null);

        boolean actual = provider.removeOrganization(realm, id);

        assertThat(actual).isFalse();
    }

    @Test
    void should_remove_organization() {
        String id = "my-id";
        String realmId = "test";
        given(realm.getId()).willReturn(realmId);
        given(em.find(OrganizationEntity.class, id))
                .willReturn(new OrganizationEntity(id, realmId, "test"));
        given(adapterFactory.create(any(), any(), any(), any(), any())).willReturn(adapter);
        given(session.getKeycloakSessionFactory()).willReturn(sessionFactory);

        boolean actual = provider.removeOrganization(realm, id);

        assertThat(actual).isTrue();
        then(em).should().remove(any());
    }

    @Test
    void should_publish_removed_event() {
        String id = "my-id";
        String realmId = "test";
        given(realm.getId()).willReturn(realmId);
        given(em.find(OrganizationEntity.class, id))
                .willReturn(new OrganizationEntity(id, realmId, "test"));
        given(adapterFactory.create(any(), any(), any(), any(), any())).willReturn(adapter);
        given(session.getKeycloakSessionFactory()).willReturn(sessionFactory);

        provider.removeOrganization(realm, id);

        ArgumentCaptor<ProviderEvent> captor = ArgumentCaptor.forClass(ProviderEvent.class);
        then(sessionFactory).should().publish(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(OrganizationModel.OrganizationRemovedEvent.class);
    }
}
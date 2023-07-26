package kz.kacd.sso.external.model.jpa;

import kz.kacd.sso.external.model.PositionModel;
import kz.kacd.sso.external.model.jpa.entity.OrganizationEntity;
import kz.kacd.sso.external.model.jpa.entity.OrganizationMemberEntity;
import kz.kacd.sso.external.model.jpa.entity.PositionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class OrganizationAdapterTest {

    @Mock
    UserModel user;
    @Mock
    KeycloakSession keycloakSession;
    @Mock
    OrganizationEntity entity;
    @Mock
    AdaptersFactory adaptersFactory;
    @Mock
    EntityManager em;
    @Mock
    RealmModel realm;
    @InjectMocks
    OrganizationAdapter organization;

    @Test
    void should_request_not_confirmed_position() {
        TypedQuery<PositionEntity> query = mock(TypedQuery.class);
        given(query.getResultStream()).willReturn(Stream.of(new PositionEntity()));
        given(em.createNamedQuery(any(), eq(PositionEntity.class))).willReturn(query);
        ArgumentCaptor<OrganizationMemberEntity> captor = ArgumentCaptor.forClass(OrganizationMemberEntity.class);
        given(adaptersFactory.create(any(), captor.capture(), any())).willReturn(mock(PositionAdapter.class));

        organization.requestPosition(PositionModel.HEAD, user);

        assertThat(captor.getValue().getConfirmed()).isFalse();
    }

    @Test
    void should_add_ceo_role_on_head_position_confirm() {
        String positionId = UUID.randomUUID().toString();
        PositionAdapter position = mock(PositionAdapter.class);
        given(position.getName()).willReturn(PositionModel.HEAD);
        given(position.getId()).willReturn(positionId);
        given(position.getUser()).willReturn(user);
        given(adaptersFactory.create(any(), any(OrganizationMemberEntity.class), any())).willReturn(position);
        OrganizationMemberEntity spy = mock(OrganizationMemberEntity.class);
        TypedQuery<PositionEntity> query = mock(TypedQuery.class);
        given(query.getResultStream()).willReturn(Stream.of(new PositionEntity()));
        given(em.createNamedQuery(any(), eq(PositionEntity.class))).willReturn(query);
        given(em.find(OrganizationMemberEntity.class, positionId)).willReturn(spy);
        ClientProvider clients = mock(ClientProvider.class);
        ClientModel lk = mock(ClientModel.class);
        given(clients.getClientByClientId(any(), any())).willReturn(lk);
        given(keycloakSession.clients()).willReturn(clients);
        RoleProvider roles = mock(RoleProvider.class);
        RoleModel ceo = mock(RoleModel.class);
        given(roles.getClientRole(any(), any())).willReturn(ceo);
        given(keycloakSession.roles()).willReturn(roles);
        given(user.hasRole(ceo)).willReturn(false);

        PositionModel head = organization.requestPosition(PositionModel.HEAD, user);
        organization.confirmPosition(head);

        then(user).should().grantRole(ceo);
    }

    @Test
    void should_revoke_ceo_role_on_head_position_revocation() {
        String positionId = UUID.randomUUID().toString();
        PositionAdapter position = mock(PositionAdapter.class);
        given(position.getName()).willReturn(PositionModel.HEAD);
        given(position.getId()).willReturn(positionId);
        given(position.getUser()).willReturn(user);
        given(adaptersFactory.create(any(), any(OrganizationMemberEntity.class), any())).willReturn(position);
        OrganizationMemberEntity spy = mock(OrganizationMemberEntity.class);
        TypedQuery<PositionEntity> query = mock(TypedQuery.class);
        given(query.getResultStream()).willReturn(Stream.of(new PositionEntity()));
        given(em.createNamedQuery(any(), eq(PositionEntity.class))).willReturn(query);
        given(em.find(OrganizationMemberEntity.class, positionId)).willReturn(spy);
        ClientProvider clients = mock(ClientProvider.class);
        ClientModel lk = mock(ClientModel.class);
        given(clients.getClientByClientId(any(), any())).willReturn(lk);
        given(keycloakSession.clients()).willReturn(clients);
        RoleProvider roles = mock(RoleProvider.class);
        RoleModel ceo = mock(RoleModel.class);
        given(roles.getClientRole(any(), any())).willReturn(ceo);
        given(keycloakSession.roles()).willReturn(roles);
        given(user.hasRole(ceo)).willReturn(true);

        PositionModel head = organization.requestPosition(PositionModel.HEAD, user);
        organization.revokePosition(head);

        then(user).should().deleteRoleMapping(ceo);
    }
}
package kz.kacd.sso.realmcontroller.keycloak.client.model;

import kz.kacd.sso.realmcontroller.k8s.crd.client.model.access.ClientAccessSpec;
import kz.kacd.sso.realmcontroller.k8s.crd.client.model.capability.CapabilitySpec;
import kz.kacd.sso.realmcontroller.k8s.model.K8sClient;
import kz.kacd.sso.realmcontroller.k8s.model.SecretData;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.List;
import java.util.Map;

import static kz.kacd.sso.realmcontroller.util.ValueUtils.defaulted;

public class ClientBuilder {

    private final ClientRepresentation target;

    ClientBuilder() {
        this.target = new ClientRepresentation();
    }

    ClientBuilder(ClientRepresentation source) {
        this.target = source;
    }

    public Client buildFrom(K8sClient source) {
        if (source.spec() == null) {
            return build();
        }

        target.setClientId(source.name());
        target.setName(defaulted(source.spec().getDisplayedName(), source.name()));
        target.setDescription(source.spec().getDescription());
        return withAccess(source.spec().getAccess())
                .withCapability(source.spec().getCapability(), source.secrets())
                .build();
    }

    private ClientBuilder withAccess(ClientAccessSpec spec) {
        var arg = spec;
        if (spec == null) {
            arg = new ClientAccessSpec();
        }

        target.setRootUrl(arg.getRootUrl());
        target.setBaseUrl(arg.getHomeUrl());
        target.setRedirectUris(defaulted(arg.getValidRedirectUris(), List.of()));
        target.setWebOrigins(defaulted(arg.getWebOrigins(), List.of()));
        return this;
    }

    private ClientBuilder withCapability(CapabilitySpec spec, Map<String, SecretData> secrets) {
        var arg = spec;
        if (spec == null) {
            arg = new CapabilitySpec();
        }

        switch (defaulted(arg.getType(), CapabilitySpec.Type.PUBLIC)) {
            case PUBLIC -> {
                target.setPublicClient(true);
                target.setBearerOnly(false);
                target.setStandardFlowEnabled(true);
            }
            case CONFIDENTIAL -> {
                target.setPublicClient(false);
                target.setBearerOnly(false);
                bindCreds(arg, secrets);
                target.setStandardFlowEnabled(true);
                target.setImplicitFlowEnabled(true);
                target.setDirectAccessGrantsEnabled(true);
                target.setServiceAccountsEnabled(true);
            }
            case BEARER_ONLY -> {
                target.setPublicClient(false);
                target.setBearerOnly(true);
            }
        }

        return this;
    }

    private void bindCreds(CapabilitySpec spec, Map<String, SecretData> secrets) {
        if (spec.getClientExistingSecret() == null) {
            return;
        }

        var secret = secrets.get(spec.getClientExistingSecret());
        target.setSecret(secret.get(spec.getClientSecretKey()));
    }

    private Client build() {
        return new Client(target);
    }
}

package kz.kacd.sso.realmcontroller.k8s;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NamespaceableResource;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.Realm;
import kz.kacd.sso.realmcontroller.k8s.crd.realm.RealmList;

import java.util.concurrent.Callable;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class TestClientUtils {

    public static void mockSecretResource(
            KubernetesClient client,
            String name,
            Callable<Secret> callback
    ) {
        var secrets = (MixedOperation<Secret, SecretList, Resource<Secret>>) mock(MixedOperation.class);
        given(client.secrets()).willReturn(secrets);
        var nonNamespaced = (NonNamespaceOperation<Secret, SecretList, Resource<Secret>>) mock(NonNamespaceOperation.class);
        given(secrets.inNamespace(anyString())).willReturn(nonNamespaced);
        var resource = (Resource<Secret>) mock(Resource.class);
        given(nonNamespaced.withName(name)).willReturn(resource);
        given(resource.get()).will(invocation -> callback.call());
    }

    public static void mockRealmListResource(
            KubernetesClient client,
            Callable<RealmList> callback
    ) {
        var realms = (MixedOperation<Realm, RealmList, Resource<Realm>>) mock(MixedOperation.class);
        given(client.resources(Realm.class, RealmList.class)).willReturn(realms);
        var nonNamespaced = (NonNamespaceOperation<Realm, RealmList, Resource<Realm>>) mock(NonNamespaceOperation.class);
        given(realms.inNamespace(anyString())).willReturn(nonNamespaced);
        given(nonNamespaced.list()).will(invocation -> callback.call());
    }

    public static NamespaceableResource<Realm> mockRealmEdit(
            KubernetesClient client,
            Realm source,
            Function<Realm, Realm> call
    ) {
        var resource = (NamespaceableResource<Realm>) mock(NamespaceableResource.class);
        given(client.resource(any(Realm.class))).willReturn(resource);
        given(resource.patchStatus()).will(invocation -> { call.apply(source); return source; });
        return resource;
    }
}

package kz.kacd.realm;

import io.fabric8.kubernetes.client.KubernetesClient;
import kz.kacd.sso.v1.Realm;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RealmProvider {
    private static final Logger log = LoggerFactory.getLogger(RealmProvider.class);

    public static final String REALM_LABEL = "kz-kacd-realm-name";

    private final KubernetesClient client;
    private final String namespace;

    public RealmProvider(
            KubernetesClient client,
            @ConfigProperty(name = "realm.controller.namespace") String namespace
    ) {
        this.client = client;
        this.namespace = namespace;
    }

    public Realm getRealm(String name) {
        log.debug("Getting realm {} ...", name);
        return client.resources(Realm.class, RealmList.class).inNamespace(namespace).withName(name).get();
    }
}

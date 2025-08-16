//package kz.kacd.sso.external.resource.profile;
//
//import kz.kacd.sso.external.resource.BaseRealmResourceProvider;
//import org.keycloak.models.KeycloakSession;
//import org.keycloak.models.RealmModel;
//
//public class ConfigurationResourceProvider extends BaseRealmResourceProvider {
//
//    protected ConfigurationResourceProvider(KeycloakSession session) {
//        super(session);
//    }
//
//    @Override
//    protected Object getRealmResource() {
//        RealmModel realm = session.getContext().getRealm();
//        ConfigurationResource res = new ConfigurationResource(session, realm);
//        res.setup();
//        return res;
//    }
//}

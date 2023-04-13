package kz.kacd.sso.external.migration.account.kafka.handler.physical;

import kz.kacd.sso.external.migration.account.kafka.handler.AuthenticationDetailsHandler;
import kz.kacd.sso.external.migration.account.kafka.handler.DrscbAccountRoleMapper;
import kz.kacd.sso.external.migration.account.kafka.handler.DrscbAttributesApplier;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import org.keycloak.models.UserModel;

public class PhysicalAccountHandler {

    private final PhysicalUserSearcher searcher;
    private final PhysicalUserCreator creator;
    private final AuthenticationDetailsHandler auth;
    private final DrscbAttributesApplier drscb;
    private final DrscbAccountRoleMapper roleMapper;

    public PhysicalAccountHandler(
            PhysicalUserSearcher searcher,
            PhysicalUserCreator creator,
            AuthenticationDetailsHandler auth,
            DrscbAttributesApplier drscb,
            DrscbAccountRoleMapper roleMapper
    ) {
        this.searcher = searcher;
        this.creator = creator;
        this.auth = auth;
        this.drscb = drscb;
        this.roleMapper = roleMapper;
    }

    public void handle(DrscbAccount account) {
        UserModel user = searcher.find(account);
        if (user == null) {
            user = creator.create(account);
            auth.applyAuthenticationDetails(account, user);
            roleMapper.map(account, user);
        }
        drscb.apply(account, user);
    }
}

package kz.kacd.sso.external.migration.account.kafka.handler.employee;

import kz.kacd.sso.external.migration.account.kafka.handler.AuthenticationDetailsHandler;
import kz.kacd.sso.external.migration.account.kafka.handler.DrscbAttributesApplier;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import org.keycloak.models.UserModel;

public class EmployeeAccountHandler {

    private final EmployeeUserSearcher searcher;
    private final EmployeeUserCreator creator;
    private final AuthenticationDetailsHandler auth;
    private final DrscbAttributesApplier applier;

    public EmployeeAccountHandler(
            EmployeeUserSearcher searcher,
            EmployeeUserCreator creator,
            AuthenticationDetailsHandler auth,
            DrscbAttributesApplier applier
    ) {
        this.searcher = searcher;
        this.creator = creator;
        this.auth = auth;
        this.applier = applier;
    }

    public void handle(DrscbAccount account) {
        UserModel user = searcher.find(account);
        if (user == null) {
            user = creator.create(account);
            auth.applyAuthenticationDetails(account, user);
        }
        applier.apply(account, user);
    }
}

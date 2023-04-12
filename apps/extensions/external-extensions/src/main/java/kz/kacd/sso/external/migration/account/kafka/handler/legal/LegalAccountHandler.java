package kz.kacd.sso.external.migration.account.kafka.handler.legal;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;
import kz.kacd.sso.external.model.OrganizationModel;

public class LegalAccountHandler {

    private final LegalOrganizationSearcher searcher;

    public LegalAccountHandler(LegalOrganizationSearcher searcher) {
        this.searcher = searcher;
    }

    public void handle(DrscbAccount account) {
        OrganizationModel org = searcher.find(account);
        if (org == null) {
            return;
        }
        if (account.getLegalName() != null) {
            org.setName(account.getLegalName());
            org.setDisplayName(account.getLegalName());
        }
    }
}

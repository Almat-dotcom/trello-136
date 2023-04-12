package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.handler.employee.EmployeeAccountHandler;
import kz.kacd.sso.external.migration.account.kafka.handler.legal.LegalAccountHandler;
import kz.kacd.sso.external.migration.account.kafka.handler.physical.PhysicalAccountHandler;
import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccount;

public class DrscbAccountHandler {

    private final PhysicalAccountHandler physicals;
    private final LegalAccountHandler legals;
    private final EmployeeAccountHandler employees;

    public DrscbAccountHandler(PhysicalAccountHandler physicals, LegalAccountHandler legals, EmployeeAccountHandler employees) {
        this.physicals = physicals;
        this.legals = legals;
        this.employees = employees;
    }

    public void handle(DrscbAccount account) {
        if (account.getKind().equals(DrscbAccount.Kind.PERSONAL_ACCOUNT)) {
            physicals.handle(account);
            return;
        }

        if (account.getKind().equals(DrscbAccount.Kind.LEGAL_ACCOUNT)) {
            legals.handle(account);
            return;
        }

        employees.handle(account);
    }
}

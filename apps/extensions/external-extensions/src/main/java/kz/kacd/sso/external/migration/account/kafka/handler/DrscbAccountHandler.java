package kz.kacd.sso.external.migration.account.kafka.handler;

import kz.kacd.sso.external.migration.account.kafka.model.DrscbAccountContext;

public interface DrscbAccountHandler {

    void handle(DrscbAccountContext context);
}

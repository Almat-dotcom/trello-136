package kz.kacd.sso.realm.config.model;

import kz.kacd.sso.v1.realmspec.security.BruteForce;
import org.keycloak.models.RealmModel;

import static kz.kacd.sso.util.ValueUtils.defaulted;
import static kz.kacd.sso.util.ValueUtils.duration;

public class BruteForceConfigurer {

    private final BruteForce source;

    public BruteForceConfigurer(BruteForce source) {
        this.source = source;
    }

    public void configure(RealmModel realm) {
        realm.setBruteForceProtected(defaulted(source.getEnabled(), false));
        if (realm.isBruteForceProtected()) {
            realm.setFailureFactor(defaulted(source.getMaxLoginFailures(), 5L).intValue());
            realm.setPermanentLockout(defaulted(source.getPermanentLockout(), false));
            if (!realm.isPermanentLockout()) {
                realm.setWaitIncrementSeconds(duration(defaulted(source.getMaxWait(), "1d")).intValue());
                realm.setMaxFailureWaitSeconds(duration(defaulted(source.getMaxWait(), "1d")).intValue());
                realm.setMaxDeltaTimeSeconds(duration(defaulted(source.getFailureResetTime(), "12h")).intValue());
                realm.setQuickLoginCheckMilliSeconds(defaulted(source.getQuickLoginMillis(), 1_000L));
                realm.setMinimumQuickLoginWaitSeconds(duration(defaulted(source.getQuickLoginWait(), "5m")).intValue());
            }
        }
    }
}

package kz.kacd.sso.external.migration.account.kafka.model;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.PositionModel;
import org.keycloak.models.UserModel;

public class DrscbAccountContext {

    private final DrscbAccount account;

    private OrganizationModel organization;
    private PositionModel position;
    private UserModel user;
    private boolean newlyCreated;

    public DrscbAccountContext(DrscbAccount account) {
        this.account = account;
    }

    public DrscbAccount getAccount() {
        return account;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public boolean isNewlyCreated() {
        return newlyCreated;
    }

    public void setNewlyCreated(boolean newlyCreated) {
        this.newlyCreated = newlyCreated;
    }

    public OrganizationModel getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationModel organization) {
        this.organization = organization;
    }

    public PositionModel getPosition() {
        return position;
    }

    public void setPosition(PositionModel position) {
        this.position = position;
    }
}

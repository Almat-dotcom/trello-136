package kz.kacd.sso.external.representation;

import kz.kacd.sso.external.model.OrganizationModel;
import kz.kacd.sso.external.model.PositionModel;

public class UserOrganizationRepresentation {

    private String orgId;
    private String bin;
    private String orgName;
    private String orgDisplayName;
    private String positionId;
    private String position;
    private Boolean confirmed;

    public UserOrganizationRepresentation() {
    }

    public UserOrganizationRepresentation(
            String orgId,
            String bin,
            String orgName,
            String orgDisplayName,
            String positionId,
            String position,
            Boolean confirmed
    ) {
        this.orgId = orgId;
        this.bin = bin;
        this.orgName = orgName;
        this.orgDisplayName = orgDisplayName;
        this.positionId = positionId;
        this.position = position;
        this.confirmed = confirmed;
    }

    public static UserOrganizationRepresentation from(OrganizationModel org, PositionModel position) {
        return new UserOrganizationRepresentation(
                org.getId(),
                org.getBin(),
                org.getName(),
                org.getDisplayName(),
                position.getId(),
                position.getName(),
                position.confirmed()
        );
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgDisplayName() {
        return orgDisplayName;
    }

    public void setOrgDisplayName(String orgDisplayName) {
        this.orgDisplayName = orgDisplayName;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}

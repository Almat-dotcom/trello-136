package kz.kacd.sso.resource.profile;

public class UpdateProfileRequest {

    private String newEmail;
    private String newPhoneNumber;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String newEmail, String newPhoneNumber) {
        this.newEmail = newEmail;
        this.newPhoneNumber = newPhoneNumber;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewPhoneNumber() {
        return newPhoneNumber;
    }

    public void setNewPhoneNumber(String newPhoneNumber) {
        this.newPhoneNumber = newPhoneNumber;
    }
}

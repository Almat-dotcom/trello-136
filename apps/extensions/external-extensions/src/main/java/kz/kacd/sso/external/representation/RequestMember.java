package kz.kacd.sso.external.representation;

import javax.validation.Valid;

public class RequestMember {

    private @Valid String position;
    private @Valid String userId;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

package kz.kacd.sso.external.model;

import org.keycloak.models.UserModel;

public interface PositionModel {

    String HEAD = "HEAD";

    String getId();

    String getName();

    String getDescription();

    UserModel getUser();

    boolean confirmed();
}

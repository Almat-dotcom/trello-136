package kz.kacd.sso.realmcontroller.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("realm")
@Data
public class RealmControllerProps {

    private String namespace;
    private String keycloak;
    private String user;
    private String password;
}

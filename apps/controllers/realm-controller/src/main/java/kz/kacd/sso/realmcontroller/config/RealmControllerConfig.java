package kz.kacd.sso.realmcontroller.config;

import kz.kacd.sso.realmcontroller.config.props.RealmControllerProps;
import kz.kacd.sso.realmcontroller.k8s.K8sClientFactory;
import kz.kacd.sso.realmcontroller.k8s.RealmRepository;
import kz.kacd.sso.realmcontroller.k8s.SecretRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RealmControllerConfig {

    private final RealmControllerProps props;
    private final K8sClientFactory k8sClientFactory;

    @Bean
    public SecretRepository secretRepository() {
        return new SecretRepository(props.getNamespace(), k8sClientFactory);
    }

    @Bean
    public RealmRepository realmRepository() {
        return new RealmRepository(props.getNamespace(), k8sClientFactory);
    }
}

package kz.kacd.sso.k8s.secret;

public interface Secret {

    String get(String key);
}

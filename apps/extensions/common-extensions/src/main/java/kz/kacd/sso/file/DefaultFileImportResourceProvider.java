package kz.kacd.sso.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import kz.kacd.sso.v1.Realm;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderEvent;

import java.io.File;
import java.util.Objects;
import java.util.stream.Stream;

public class DefaultFileImportResourceProvider implements FileImportResourceProvider {
    private static final String IMPORT_DIR = "/opt/keycloak/.kacd/import";

    private final KeycloakSession session;

    public DefaultFileImportResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void findRealmResources() {
        files(IMPORT_DIR).map(this::parse)
                .forEach(it -> session.getKeycloakSessionFactory().publish(foundResource(it)));
    }

    private ProviderEvent foundResource(Realm source) {
        return new RealmImportResourceFound() {

            @Override
            public KeycloakSessionFactory getSession() {
                return session.getKeycloakSessionFactory();
            }

            @Override
            public Realm getResource() {
                return source;
            }
        };
    }

    private Realm parse(File source) {
        try {
            return new YAMLMapper().readValue(source, Realm.class);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse file of realm!", e);
        }
    }

    private Stream<File> files(String dir) {
        File[] files = new File(dir).listFiles();
        if (files == null) {
            return Stream.empty();
        }
        return Stream.of(files).filter(file -> !file.isDirectory());
    }

    @Override
    public void close() {
        // Nothing to close
    }
}

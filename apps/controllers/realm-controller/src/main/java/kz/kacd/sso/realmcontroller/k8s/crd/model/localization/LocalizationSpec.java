package kz.kacd.sso.realmcontroller.k8s.crd.model.localization;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
public class LocalizationSpec {

    @JsonPropertyDescription("If true internationalization will be enabled")
    private Boolean i17n;
    @JsonPropertyDescription("List of supported locales")
    private List<String> supportedLocales;
    @JsonPropertyDescription("Default locale if no one is defined")
    private String defaultLocale;
}

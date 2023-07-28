package kz.kacd.sso.external.sign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SignatureSubject {

    private static final String LEGAL = "1.2.398.3.3.4.1.2";
    private static final String CEO = "1.2.398.3.3.4.1.2.1";
    private static final String EMPLOYEE = "1.2.398.3.3.4.1.2.5";

    private String iin;
    private String bin;
    private String commonName;
    private String surName;
    private String givenName;
    private List<String> extendedKeyUsage;

    public boolean legal() {
        return extendedKeyUsage.contains(LEGAL);
    }

    public boolean ceo() {
        return extendedKeyUsage.contains(CEO);
    }

    public boolean employee() {
        return extendedKeyUsage.contains(EMPLOYEE);
    }

    public String getFirstName() {
        if (commonName == null) {
            return null;
        }
        return commonName.replace(surName + " ", "");
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public List<String> getExtendedKeyUsage() {
        return extendedKeyUsage;
    }

    public void setExtendedKeyUsage(List<String> extendedKeyUsage) {
        this.extendedKeyUsage = extendedKeyUsage;
    }
}

package kz.kacd.sso.external.sign;

import kz.kacd.sso.external.sign.exception.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import java.util.Arrays;
import java.util.Date;

public class DocumentSignature {
    private static final Logger log = Logger.getLogger(DocumentSignature.class);

    private static final String SIGNATURE = "ds:Signature";
    private static final String VALID_ISSUER = "CN=ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (GOST), C=KZ";

    private final Element sign;

    private String iin;
    private String bin;

    public DocumentSignature(Document document) {
        sign = extractSignature(document);
    }

    private Element extractSignature(Document document) {
        log.debug("Extracting sing from document ...");
        Element rootEl = (Element) document.getFirstChild();
        NodeList list = rootEl.getElementsByTagName(SIGNATURE);
        if (list.getLength() == 0) {
            log.debug("Signature element not found in document!");
            throw new SignatureNotFoundException();
        }

        return (Element) list.item(list.getLength() - 1);
    }

    public void validate() {
        try {
            XMLSignature xmlSignature = new XMLSignature(sign, "");
            KeyInfo key = xmlSignature.getKeyInfo();
            X509Certificate certificate = key.getX509Certificate();
            if (certificate == null) {
                log.debug("Invalid signature! Certificate was not found!");
                throw new CertificateNotFoundException();
            }

            boolean correct = xmlSignature.checkSignatureValue(certificate);
            if (!correct) {
                log.debug("Signature value is incorrect!");
                throw new InvalidSignatureValueException();
            }

            validate(certificate);
            extractSubject(certificate);
        } catch (XMLSecurityException e) {
            log.debug("Error on validating signature!", e);
            throw new InvalidSignatureException();
        }
    }

    private void validate(X509Certificate certificate) {
        if (notActive(certificate)) {
            log.debug("Certificate expired or not started live period!");
            throw new InvalidCertificateException();
        }

        if (invalidIssuer(certificate)) {
            log.debug("Certificate issuer is invalid!");
            throw new InvalidCertificateException();
        }
    }

    private boolean notActive(X509Certificate certificate) {
        return new Date().before(certificate.getNotBefore())
                || new Date().after(certificate.getNotAfter());
    }

    private boolean invalidIssuer(X509Certificate certificate) {
        return !VALID_ISSUER.equals(certificate.getIssuerX500Principal().getName());
    }

    private void extractSubject(X509Certificate certificate) {
        X500Principal principal = certificate.getSubjectX500Principal();
        String dn = principal.getName();

        Arrays.stream(dn.split(","))
                .map(String::trim)
                .filter(it -> it.startsWith("SERIALNUMBER") || it.startsWith("OU"))
                .forEach(it -> {
                    if (it.startsWith("SERIALNUMBER")) {
                        iin = it.replace("SERIALNUMBER=IIN", "");
                        return;
                    }
                    if (it.startsWith("OU")) {
                        bin = it.replace("OU=BIN", "");
                    }
                });
    }

    public String getIin() {
        return iin;
    }

    public String getBin() {
        return bin;
    }
}

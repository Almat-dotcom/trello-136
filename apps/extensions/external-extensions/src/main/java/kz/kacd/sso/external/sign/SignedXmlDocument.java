package kz.kacd.sso.external.sign;

import org.jboss.logging.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class SignedXmlDocument {
    private static final Logger log = Logger.getLogger(SignedXmlDocument.class);

    private Document doc;
    private Throwable error;
    private DocumentSignature signature;

    public SignedXmlDocument(String source) {
        initDoc(source);
    }

    private void initDoc(String source) {
        try {
            log.debug("Initializing doc ...");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.debug("Error on initializing document!", e);
            this.error = e;
        }
    }

    public void validate() {
        try {
            signature = new DocumentSignature(doc);
            signature.validate();
        } catch (Exception e) {
            error = e;
        }
    }

    public Throwable getError() {
        return error;
    }

    public String getIin() {
        return signature != null ? signature.getIin() : null;
    }

    public String getBin() {
        return signature != null ? signature.getBin() : null;
    }
}

package kz.kacd.sso.external.representation;

import java.util.List;

public class Content<T> {

    private final List<T> content;

    public Content(List<T> content) {
        this.content = content;
    }

    public List<T> getContent() {
        return content;
    }
}

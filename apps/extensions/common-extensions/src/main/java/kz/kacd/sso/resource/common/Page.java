package kz.kacd.sso.resource.common;

import java.util.List;

public class Page<T> {

    private final int page;
    private final int elementsCount;
    private final int limit;
    private final int totalPages;
    private final long totalElements;
    private final List<T> content;

    public Page(int page, int elementsCount, int limit, int totalPages, long totalElements, List<T> content) {
        this.page = page;
        this.elementsCount = elementsCount;
        this.limit = limit;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<T> getContent() {
        return content;
    }
}

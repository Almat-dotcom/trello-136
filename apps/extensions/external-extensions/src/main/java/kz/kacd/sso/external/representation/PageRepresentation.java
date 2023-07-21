package kz.kacd.sso.external.representation;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageRepresentation<T> {

    private final long totalElements;
    private final int from;
    private final int limit;
    private final List<T> content;

    public PageRepresentation(long totalElements, int from, int limit, List<T> content) {
        this.totalElements = totalElements;
        this.from = from;
        this.limit = limit;
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getFrom() {
        return from;
    }

    public int getLimit() {
        return limit;
    }

    public List<T> getContent() {
        return content;
    }

    public <R> PageRepresentation<R> map(Function<T, R> mapping) {
        return new PageRepresentation<>(
                totalElements,
                from,
                limit,
                content.stream().map(mapping).collect(Collectors.toList())
        );
    }
}

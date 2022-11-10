package kz.kacd.sso.realmcontroller.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

/**
 * Common response envelope of kubernetes repositories.
 */
public abstract sealed class OperationResponse<T> permits OperationResponse.Success, OperationResponse.Failure {

    public static <T> OperationResponse<T> success(T data) {
        return new Success<>(data);
    }

    public static <T> OperationResponse<T> notFound(String message) {
        return new Failure<>(K8sFailures.NOT_FOUND, message);
    }

    public static <T> OperationResponse<T> internalError(String message) {
        return new Failure<>(K8sFailures.INTERNAL_ERROR, message);
    }

    public <R> OperationResponse<R> map(Function<T, R> f) {
        if (this instanceof OperationResponse.Success<T> s) {
            return success(f.apply(s.data));
        }
        var failure = (Failure<?>) this;
        return new Failure<>(failure.kind, failure.message);
    }

    /**
     * Represents successful response
     */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Success<T> extends OperationResponse<T> {
        private final T data;
    }

    /**
     * Represents failure.
     */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Failure<T> extends OperationResponse<T> {
        private final K8sFailures kind;
        private final String message;
    }

    public enum K8sFailures {
        NOT_FOUND,
        INTERNAL_ERROR
    }
}

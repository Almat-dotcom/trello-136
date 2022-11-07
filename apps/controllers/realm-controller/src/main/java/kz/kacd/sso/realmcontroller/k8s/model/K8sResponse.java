package kz.kacd.sso.realmcontroller.k8s.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Common response envelope of kubernetes repositories.
 */
public abstract sealed class K8sResponse<T> permits K8sResponse.Success, K8sResponse.Failure {

    public static <T> K8sResponse<T> success(T data) {
        return new Success<>(data);
    }

    public static <T> K8sResponse<T> notFound(String message) {
        return new Failure<>(K8sFailures.NOT_FOUND, message);
    }

    public static <T> K8sResponse<T> internalError(String message) {
        return new Failure<>(K8sFailures.INTERNAL_ERROR, message);
    }

    /**
     * Represents successful response
     */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Success<T> extends K8sResponse<T> {
        private final T data;
    }

    /**
     * Represents failure.
     */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Failure<T> extends K8sResponse<T> {
        private final K8sFailures kind;
        private final String message;
    }

    public enum K8sFailures {
        NOT_FOUND,
        INTERNAL_ERROR
    }
}

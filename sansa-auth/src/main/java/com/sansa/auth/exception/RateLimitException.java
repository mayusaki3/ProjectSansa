package com.sansa.auth.exception;

/**
 * レート制限に達した場合にスローされる実行時例外。
 * <p>
 * Controller 層では {@code ApiExceptionHandler} が 429 Too Many Requests
 * + application/problem+json にマッピングします。
 */
public class RateLimitException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RateLimitException(String message) { super(message); }
    public RateLimitException(String message, Throwable cause) { super(message, cause); }
}

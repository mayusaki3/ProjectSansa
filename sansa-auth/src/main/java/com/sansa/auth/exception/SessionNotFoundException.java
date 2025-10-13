package com.sansa.auth.exception;

/**
 * セッションが見つからない場合にスローされる実行時例外。
 * <p>
 * 主に Service 層から投げられ、Controller 層では {@code ApiExceptionHandler}
 * が 404 Not Found + application/problem+json にマッピングします。
 */
public class SessionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SessionNotFoundException(String message) { super(message); }
    public SessionNotFoundException(String message, Throwable cause) { super(message, cause); }
}

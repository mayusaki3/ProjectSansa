package com.sansa.auth.exception;

/**
 * 認証情報（ID/パスワード等）が不正な場合にスローされる実行時例外。
 * <p>
 * Controller 層では {@code ApiExceptionHandler} が 401 Unauthorized
 * + application/problem+json にマッピングします。
 */
public class InvalidCredentialsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(String message) { super(message); }
    public InvalidCredentialsException(String message, Throwable cause) { super(message, cause); }
}

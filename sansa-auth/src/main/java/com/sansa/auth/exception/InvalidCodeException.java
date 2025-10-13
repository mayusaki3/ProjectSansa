package com.sansa.auth.exception;

/**
 * メール確認コード / OTP / TOTP 等の検証コードが不正・無効・期限切れのときに
 * スローされる実行時例外。
 * <p>
 * Controller 層では {@code ApiExceptionHandler} が 400 Bad Request
 * もしくは 422 Unprocessable Entity 等、要件に応じた
 * application/problem+json にマッピングします。
 */
public class InvalidCodeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidCodeException(String message) { super(message); }
    public InvalidCodeException(String message, Throwable cause) { super(message, cause); }
}

package com.sansa.auth.exception;

/**
 * リクエストが不正（バリデーション不備など）の場合に投げる例外。
 * 典型的には 400 Bad Request にマッピングする。
 *
 * <p>message ではなく「エラーコード（識別子）」を扱う。
 */
public class BadRequestException extends DomainException {
    private static final long serialVersionUID = 1L;

    public BadRequestException(String code) {
        super(code);
    }

    public BadRequestException(String code, Throwable cause) {
        super(code, cause);
    }

    public BadRequestException(String code, Object... args) {
        super(code, args);
    }

    public BadRequestException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

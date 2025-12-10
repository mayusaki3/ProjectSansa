package com.sansa.auth.exception;

/**
 * 認証が必要／不十分な場合に投げる例外。
 * 典型的には 401 Unauthorized にマッピングする。
 *
 * 役割:
 *   - 「認証されていない／セッションが無い／資格情報が無い」などの状態を表す。
 *   - エラーコードは呼び出し側が指定する（例: "auth.unauthorized"）。
 */
public class UnauthorizedException extends DomainException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String code) {
        super(code);
    }

    public UnauthorizedException(String code, Throwable cause) {
        super(code, cause);
    }

    public UnauthorizedException(String code, Object... args) {
        super(code, args);
    }

    public UnauthorizedException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

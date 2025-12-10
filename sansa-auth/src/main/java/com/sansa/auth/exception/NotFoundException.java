package com.sansa.auth.exception;

/**
 * 対象リソースが見つからない場合に投げる例外。
 * 典型的には 404 Not Found にマッピングする。
 *
 * 役割:
 *   - 存在しないユーザー/セッション/トークンなどへのアクセスを表す。
 */
public class NotFoundException extends DomainException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String code) {
        super(code);
    }

    public NotFoundException(String code, Throwable cause) {
        super(code, cause);
    }

    public NotFoundException(String code, Object... args) {
        super(code, args);
    }

    public NotFoundException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

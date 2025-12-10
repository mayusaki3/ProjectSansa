package com.sansa.auth.exception;

/**
 * トークン期限切れを表すドメイン例外。
 * 典型的には 401 にマッピングされる。
 *
 * 役割:
 *   - 有効期限切れの AccessToken / RefreshToken などを示す。
 */
public class TokenExpiredException extends DomainException {
    private static final long serialVersionUID = 1L;

    public TokenExpiredException(String code) {
        super(code);
    }

    public TokenExpiredException(String code, Throwable cause) {
        super(code, cause);
    }

    public TokenExpiredException(String code, Object... args) {
        super(code, args);
    }

    public TokenExpiredException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

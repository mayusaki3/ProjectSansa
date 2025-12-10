package com.sansa.auth.exception;

/**
 * Refresh Token の再利用が検知された場合のドメイン例外。
 * 典型的には 401 にマッピングされる。
 *
 * 役割:
 *   - セキュリティインシデントとして扱うべき RT 再利用を表す。
 */
public class TokenReusedException extends DomainException {
    private static final long serialVersionUID = 1L;

    public TokenReusedException(String code) {
        super(code);
    }

    public TokenReusedException(String code, Throwable cause) {
        super(code, cause);
    }

    public TokenReusedException(String code, Object... args) {
        super(code, args);
    }

    public TokenReusedException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

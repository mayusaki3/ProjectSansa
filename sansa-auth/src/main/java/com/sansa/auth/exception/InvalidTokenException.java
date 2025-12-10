package com.sansa.auth.exception;

/**
 * 不正トークン（tv不一致/改竄など）を表すドメイン例外。
 * 典型的には 401 にマッピングされる。
 *
 * 役割:
 *   - 署名不正 / tv 不整合など「信頼できないトークン」を示す。
 */
public class InvalidTokenException extends DomainException {
    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String code) {
        super(code);
    }

    public InvalidTokenException(String code, Throwable cause) {
        super(code, cause);
    }

    public InvalidTokenException(String code, Object... args) {
        super(code, args);
    }

    public InvalidTokenException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

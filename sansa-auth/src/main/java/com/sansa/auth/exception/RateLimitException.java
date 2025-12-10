package com.sansa.auth.exception;

/**
 * レート制限に達した場合にスローされるドメイン例外。
 * 典型的には 429 Too Many Requests にマッピングされる。
 *
 * 役割:
 *   - 「短時間に許容量を超えるリクエストが行われた」状況を表す。
 */
public class RateLimitException extends DomainException {
    private static final long serialVersionUID = 1L;

    public RateLimitException(String code) {
        super(code);
    }

    public RateLimitException(String code, Throwable cause) {
        super(code, cause);
    }

    public RateLimitException(String code, Object... args) {
        super(code, args);
    }

    public RateLimitException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

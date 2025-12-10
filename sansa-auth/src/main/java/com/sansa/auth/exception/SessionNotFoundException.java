package com.sansa.auth.exception;

/**
 * セッションが見つからない場合にスローされるドメイン例外。
 * 典型的には 404 Not Found にマッピングされる。
 *
 * 役割:
 *   - 「セッションID自体が存在しない／既に破棄されている」状態を表す。
 */
public class SessionNotFoundException extends DomainException {
    private static final long serialVersionUID = 1L;

    public SessionNotFoundException(String code) {
        super(code);
    }

    public SessionNotFoundException(String code, Throwable cause) {
        super(code, cause);
    }

    public SessionNotFoundException(String code, Object... args) {
        super(code, args);
    }

    public SessionNotFoundException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

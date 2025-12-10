package com.sansa.auth.exception;

/**
 * 409 Conflict を表すドメイン例外。
 *
 * 役割:
 *   - リソースの重複や状態競合（例: 既に存在するメール/クレデンシャルの登録）を示す。
 */
public class ConflictException extends DomainException {
    private static final long serialVersionUID = 1L;

    public ConflictException(String code) {
        super(code);
    }

    public ConflictException(String code, Throwable cause) {
        super(code, cause);
    }

    public ConflictException(String code, Object... args) {
        super(code, args);
    }

    public ConflictException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

package com.sansa.auth.exception;

/**
 * 認証情報（ID/パスワード等）が不正な場合にスローされるドメイン例外。
 * 典型的には 401 Unauthorized にマッピングされる。
 *
 * 役割:
 *   - 「ユーザーは存在するが、資格情報が一致しない」ケースを表す。
 */
public class InvalidCredentialsException extends DomainException {
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(String code) {
        super(code);
    }

    public InvalidCredentialsException(String code, Throwable cause) {
        super(code, cause);
    }

    public InvalidCredentialsException(String code, Object... args) {
        super(code, args);
    }

    public InvalidCredentialsException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

package com.sansa.auth.exception;

/**
 * メール確認コード / OTP / TOTP 等の検証コードが不正・無効・期限切れのときに
 * スローされるドメイン例外。
 *
 * 役割:
 *   - コードのフォーマット不正／不一致／期限切れなどを 1 つの種別として表す。
 */
public class InvalidCodeException extends DomainException {
    private static final long serialVersionUID = 1L;

    public InvalidCodeException(String code) {
        super(code);
    }

    public InvalidCodeException(String code, Throwable cause) {
        super(code, cause);
    }

    public InvalidCodeException(String code, Object... args) {
        super(code, args);
    }

    public InvalidCodeException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

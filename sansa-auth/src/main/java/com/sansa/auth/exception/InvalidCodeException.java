package com.sansa.auth.exception;

/** メール/OTP/TOTP などの検証コード不正時に投げる実行時例外 */
public class InvalidCodeException extends RuntimeException {
    private final String codeType;   // "email", "totp", "recovery" など
    private final String reason;     // "mismatch", "expired", "consumed" など

    public InvalidCodeException(String message) {
        super(message);
        this.codeType = null;
        this.reason = null;
    }

    public InvalidCodeException(String message, String codeType, String reason) {
        super(message);
        this.codeType = codeType;
        this.reason = reason;
    }

    public String getCodeType() { return codeType; }
    public String getReason() { return reason; }
}

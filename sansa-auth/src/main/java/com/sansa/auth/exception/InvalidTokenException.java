package com.sansa.auth.exception;

/**
 * 不正トークン（tv不一致/改竄など）。401 invalid_token へマップ。
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() { super(); }
    public InvalidTokenException(String message) { super(message); }
    public InvalidTokenException(String message, Throwable cause) { super(message, cause); }
}

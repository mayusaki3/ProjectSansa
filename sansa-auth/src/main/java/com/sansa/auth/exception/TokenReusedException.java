package com.sansa.auth.exception;

/**
 * RT再利用検知。401 token_reused へマップ。
 */
public class TokenReusedException extends RuntimeException {
    public TokenReusedException() { super(); }
    public TokenReusedException(String message) { super(message); }
    public TokenReusedException(String message, Throwable cause) { super(message, cause); }
}

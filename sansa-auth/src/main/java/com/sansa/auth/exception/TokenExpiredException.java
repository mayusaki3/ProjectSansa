package com.sansa.auth.exception;

/**
 * トークン期限切れ（401 token_expired へマップ）。
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() { super(); }
    public TokenExpiredException(String message) { super(message); }
    public TokenExpiredException(String message, Throwable cause) { super(message, cause); }
}

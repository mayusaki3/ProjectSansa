package com.sansa.auth.exception;

/**
 * 認証が必要／不十分な場合に投げる例外。
 * 典型的には 401 Unauthorized にマッピングする。
 */
public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String type) { super(type); }
    public UnauthorizedException(String type, Throwable cause) { super(type, cause); }
}

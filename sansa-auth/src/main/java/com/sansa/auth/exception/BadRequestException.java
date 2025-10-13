package com.sansa.auth.exception;

/**
 * リクエストが不正（バリデーション不備など）の場合に投げる例外。
 * 典型的には 400 Bad Request にマッピングする。
 */
public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BadRequestException(String type) { super(type); }
    public BadRequestException(String type, Throwable cause) { super(type, cause); }
}

package com.sansa.auth.exception;

/**
 * 対象リソースが見つからない場合に投げる例外。
 * 典型的には 404 Not Found にマッピングする。
 */
public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String type) { super(type); }
    public NotFoundException(String type, Throwable cause) { super(type, cause); }
}

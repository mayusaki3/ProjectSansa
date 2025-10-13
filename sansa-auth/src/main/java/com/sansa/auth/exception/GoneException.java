package com.sansa.auth.exception;

/**
 * 対象がすでに消滅／有効期限切れなどの場合に投げる例外。
 * 典型的には 410 Gone にマッピングする。
 */
public class GoneException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public GoneException(String type) { super(type); }
    public GoneException(String type, Throwable cause) { super(type, cause); }
}

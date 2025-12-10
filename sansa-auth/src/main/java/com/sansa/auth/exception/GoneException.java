package com.sansa.auth.exception;

/**
 * 対象がすでに消滅／有効期限切れなどの場合に投げる例外。
 * 典型的には 410 Gone にマッピングする。
 *
 * 役割:
 *   - メール確認用トークンやセッションなど、
 *     「以前は存在したが、現在は破棄済み／期限切れ」であることを表す。
 */
public class GoneException extends DomainException {
    private static final long serialVersionUID = 1L;

    public GoneException(String code) {
        super(code);
    }

    public GoneException(String code, Throwable cause) {
        super(code, cause);
    }

    public GoneException(String code, Object... args) {
        super(code, args);
    }

    public GoneException(String code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}

package com.sansa.auth.exception;

/**
 * 409 Conflict を表す実行時例外。
 *
 * 役割:
 *   - リソースの重複や状態競合（例: 既に存在するメール/クレデンシャルの登録）を示す。
 *
 * 注意:
 *   - コントローラ層では ApiExceptionHandler で 409 にマッピングする想定。
 *   - メッセージには「何が競合したか」が分かる短い識別子/説明を入れる。
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

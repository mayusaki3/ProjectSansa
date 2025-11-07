package com.sansa.auth.service.port;

/** パスワードPHCポート（Argon2id想定）。 */
public interface PasswordPort {
    /** 生→PHC */
    String hash(String raw);
    /** 検証 */
    boolean verify(String raw, String phc);
    /** パラメータ変更時の再ハッシュ判定。初期はfalseで可。 */
    default boolean needsRehash(String phc) { return false; }
}

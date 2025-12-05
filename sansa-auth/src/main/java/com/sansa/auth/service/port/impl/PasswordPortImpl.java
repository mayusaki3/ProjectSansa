package com.sansa.auth.service.port.impl;

import com.sansa.auth.service.port.PasswordPort;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Argon2id 実装。PHC形式で保存するパスワードハッシュポートの実装クラス。
 *
 * 役割:
 * - 生パスワードを Argon2id でハッシュ化（PHC形式）
 * - 生パスワードと既存PHCの一致判定
 * - 将来パラメータ見直し時の rehash 判定（当面は false 固定）
 *
 * 注意点:
 * - Spring Security の Argon2PasswordEncoder に委譲しているため、
 *   パラメータ変更時は encoder の設定を見直すこと。
 */
@Component
public class PasswordPortImpl implements PasswordPort {

    /** Argon2id のエンコーダ（DIで注入される想定）。 */
    private final Argon2PasswordEncoder enc;

    /**
     * コンストラクタ。
     *
     * @param enc 使用する Argon2PasswordEncoder インスタンス
     */
    public PasswordPortImpl(Argon2PasswordEncoder enc) {
        this.enc = enc;
    }

    /**
     * 生パスワードを PHC 形式のハッシュに変換する。
     *
     * @param raw 生パスワード
     * @return PHC 形式のハッシュ文字列
     */
    @Override
    public String hash(String raw) {
        return enc.encode(raw);
    }

    /**
     * 生パスワードと既存の PHC ハッシュが一致するか検証する。
     *
     * @param raw 生パスワード
     * @param phc 既存の PHC ハッシュ文字列
     * @return 一致すれば true
     */
    @Override
    public boolean verify(String raw, String phc) {
        return enc.matches(raw, phc);
    }

    /**
     * パラメータ変更時に rehash が必要かを判定する。
     * 当面は常に false（再ハッシュ不要）とする。
     *
     * @param phc 既存の PHC ハッシュ文字列
     * @return 現状は常に false
     */
    @Override
    public boolean needsRehash(String phc) {
        return false;
    }
}

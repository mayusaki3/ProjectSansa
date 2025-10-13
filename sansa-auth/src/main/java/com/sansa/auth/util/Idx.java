package com.sansa.auth.util;

/**
 * 各種識別子の正規化ユーティリティ。
 *
 * <p>想定機能:
 * <ul>
 *   <li>{@code normEmail(String)}: 前後空白除去 + 小文字化 + （必要なら）全角→半角変換</li>
 *   <li>{@code normIdentifier(String)}: ログインID（メール/ユーザー名等）を共通形式へ正規化</li>
 * </ul>
 *
 * <p>注意:
 * <ul>
 *   <li>メールアドレスの厳密な仕様（RFC）は複雑なため、プロダクションではバリデーション方針を明確化</li>
 *   <li>正規化に伴うロス（大文字小文字の区別など）についてはドメイン要件で決定</li>
 * </ul>
 */
public final class Idx {

    /**
     * メールアドレスを正規化する（トリム + 小文字化）。
     *
     * @param email 入力メール
     * @return 正規化済みメール（{@code null} 入力時は {@code null}）
     */
    public static String normEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /**
     * メールアドレス形式の識別子を正規化する（トリム + 小文字化）。
     * メール形式でない場合は {@code null} を返す。
     *
     * @param id 入力識別子
     * @return 正規化済みメール or {@code null}
     */
    public static String normEmailOrNull(String id) {
        if (id == null) return null;
        String s = id.trim();
        return s.contains("@") ? s.toLowerCase() : null;
    }
    private Idx() {}
}

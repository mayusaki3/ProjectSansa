package com.sansa.auth.util;

import io.jsonwebtoken.Claims;

/**
 * アクセストークン／リフレッシュトークンの発行・解析を司る薄いファサード。
 *
 * <p>内部では {@link JwtProvider} を用いますが、ドメイン層やサービス層が
 * JJWT などの詳細に依存しないよう、最小限の API に絞っています。</p>
 *
 * <p>本インターフェースは現状の {@link JwtProvider} 実装に合わせ、
 * リフレッシュトークンのバージョンは "tv" クレーム(String) で管理します。
 * 旧実装で使っていた refreshId(jti) は扱いません。</p>
 */
public interface TokenIssuer {

    /**
     * アクセストークンを発行します。
     * <p>有効期間は {@link JwtProvider} の設定（minutes）に従います。</p>
     *
     * @param userId サブジェクト（ユーザー識別子）
     * @param tokenVersion トークンバージョン。"tv" クレームに格納（null 可）
     * @return 署名済みJWT文字列（アクセストークン）
     * @throws IllegalStateException 設定が不正な場合など
     */
    String issueAccessToken(String userId, int tokenVersion);

    /**
     * リフレッシュトークンを発行します。
     * <p>有効期間は {@link JwtProvider} の設定（days）に従います。</p>
     *
     * @param userId       サブジェクト（ユーザー識別子）
     * @param refreshId   "jti" クレームに格納する一意な識別子
     * @return 署名済みJWT文字列（リフレッシュトークン）
     * @throws IllegalStateException 設定が不正な場合など
     */
    String issueRefreshToken(String userId, String refreshId);

    /**
     * リフレッシュトークンを解析し、署名検証・有効期限検証を行った上で
     * 必要最小限の情報（userId, tokenVersion）を返します。
     *
     * <p>想定する例外は {@link JwtProvider#parse(String)} に準じます。
     * 例：署名不正、期限切れ、形式不正など。</p>
     *
     * @param refreshToken 署名済みJWT文字列（リフレッシュトークン）
     * @return userId と tokenVersion を持つ結果
     */
    RefreshParseResult parseRefresh(String refreshToken);

    /**
     * {@link #parseRefresh(String)} の戻り値用レコード。
     *
     * @param userId       サブジェクト（ユーザー識別子）
     * @param refreshId    "jti" クレーム。未設定なら null
     */
    record RefreshParseResult(String userId, String refreshId) {

        /**
         * {@link Claims} から必要な項目だけを安全に抜き出すヘルパー。
         * <p>外部公開しておくとテストでも再利用できます。</p>
         */
        public static RefreshParseResult fromClaims(Claims claims) {
        String subject = claims.getSubject();
        String tv = claims.get("tv", String.class); // 無ければ null
        return new RefreshParseResult(subject, tv);
        }
    }
}

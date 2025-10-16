package com.sansa.auth.util;

/**
 * アクセストークン／リフレッシュトークンの発行・解析を司る薄いファサード。
 *
 * <p>内部実装では {@code JwtProvider} 等を用いるが、呼び出し側（サービス層・ドメイン層）
 * が JJWT などの外部ライブラリ詳細に依存しないよう、最小限の API に絞る。</p>
 *
 * <h3>クレーム設計</h3>
 * <ul>
 *   <li>アクセストークン:
 *     <ul>
 *       <li>{@code sub}: ユーザID（必須）</li>
 *       <li>{@code tv}: tokenVersion（必須）</li>
 *     </ul>
 *   </li>
 *   <li>リフレッシュトークン:
 *     <ul>
 *       <li>{@code sub}: ユーザID（必須）</li>
 *       <li>{@code jti}: refreshId（必須／一意）</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>注意: インターフェース層では JJWT の型（{@code Claims} 等）を一切露出しない。
 * 解析結果はドメイン寄りの値オブジェクト {@link RefreshParseResult} に畳み込む。</p>
 */
public interface TokenIssuer {

    /**
     * アクセストークンを発行する。
     *
     * @param userId       ユーザID（{@code sub}）
     * @param tokenVersion トークンバージョン（{@code tv} に格納）
     * @return 署名済みアクセストークン（JWT）
     */
    String issueAccessToken(String userId, int tokenVersion);

    /**
     * リフレッシュトークンを発行する。
     *
     * @param userId    ユーザID（{@code sub}）
     * @param refreshId リフレッシュトークンの識別子（{@code jti}）
     * @return 署名済みリフレッシュトークン（JWT）
     */
    String issueRefreshToken(String userId, String refreshId);

    /**
     * リフレッシュトークンを解析し、必要最小限の情報を取り出す。
     * <ul>
     *   <li>{@code sub} → userId</li>
     *   <li>{@code jti} → refreshId</li>
     * </ul>
     *
     * @param refreshToken 署名済みリフレッシュトークン（JWT）
     * @return 解析結果（userId と refreshId）
     * @throws IllegalArgumentException 不正 or 失効などで読み取れない場合
     */
    RefreshParseResult parseRefresh(String refreshToken);

    /**
     * リフレッシュトークン解析の結果を保持する値オブジェクト。
     * <p>JJWT の {@code Claims} など外部型はここに露出させない。</p>
     *
     * @param userId    {@code sub}
     * @param refreshId {@code jti}
     */
    record RefreshParseResult(String userId, String refreshId) { }

    /**
     * リフレッシュトークンのローテーションに使う新しい JTI を生成する。
     * 実装は衝突が事実上起こらないランダム文字列（UUID など）を返せばよい。
     * 
     * @return 新規 JTI（例：UUID v4 の文字列）
     */
    String newRefreshId();
}

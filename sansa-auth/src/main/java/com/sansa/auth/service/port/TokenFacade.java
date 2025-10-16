package com.sansa.auth.service.port;

import java.util.List;

/**
 * TokenFacade は、MFA 完了後や認証フローの終端で
 * 「アクセストークン／リフレッシュトークンのペア」を発行・ローテーションするための
 * ドメインサービス向け “ポート（インターフェース）” です。
 *
 * 役割:
 * - アプリケーション層（例: MfaServiceImpl）が JWT 発行ロジックに直接依存しないようにする
 * - 下位の TokenIssuer / JwtProvider の API 変更を、このファサードの実装に閉じ込める
 *
 * 注意:
 * - 実装は infrastructure 側（例: JwtProvider を使う実装）に置き、DI で注入します。
 * - 戻り値 TokenPair はここで定義するネスト型です。
 */
public interface TokenFacade {

    /**
     * アクセス／リフレッシュの発行結果を返す不変 DTO。
     * MfaWiringConfig などから参照する場合は
     *  import com.sansa.auth.service.port.TokenFacade.TokenPair;
     * とインポートしてください。
     */
    public static record TokenPair(String accessToken, String refreshToken) {}

    /**
     * 認証（および必要なら MFA）を完了した直後に発行する。
     * ユーザーIDと（必要なら）権限・ロールを受け取り、AT/RT のペアを返す。
     *
     * @param userId 対象ユーザーID
     * @param roles  トークンに反映したいロール等（使わない実装なら無視してOK）
     * @return アクセストークン／リフレッシュトークンのペア
     */
    TokenPair issueAfterAuth(String userId, List<String> roles);

    /**
     * リフレッシュトークンのローテーションを行い、新しい AT/RT を発行する。
     * 古い RT の取り扱い（無効化や再利用検知など）は、呼び出し側や下位サービスの責務に委ねる。
     *
     * @param userId       対象ユーザーID
     * @param oldRefreshId 直前まで使用していたリフレッシュID（監査や無効化に使う場合）
     * @return 新しいアクセストークン／リフレッシュトークンのペア
     */
    TokenPair rotate(String userId, String oldRefreshId);
}

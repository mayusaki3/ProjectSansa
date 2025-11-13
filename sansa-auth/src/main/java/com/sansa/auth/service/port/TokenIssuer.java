package com.sansa.auth.service.port;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * トークン発行および解析のポート（ドメイン外部＝JWT実装への依存を隔離する）。
 *
 * - アプリ層はこのインターフェースのみに依存し、具体的なJWT実装（JJWTなど）は adapter 層(impl) に閉じ込める。
 * - 名前と配置は他ポート（TokenFacade など）と揃えるため service.port 配下とする。
 */
public interface TokenIssuer {

    /**
     * アクセストークンを発行する。
     *
     * @param userId       対象ユーザーID（JWTの sub）
     * @param tokenVersion トークンバージョン（tv）。強制ログアウトや一括失効に使用
     * @param issuedAt     発行時刻（iat）
     * @param ttl          有効期間（exp = iat + ttl）
     * @param amr          Authentication Method Reference（例: ["pwd","mfa"]）
     * @param sessionId    セッションID（sid）。監査やブラックリスト管理で利用
     * @return 署名済みアクセストークン（JWT）
     */
    String issueAccess(String userId,
                       int tokenVersion,
                       Instant issuedAt,
                       Duration ttl,
                       List<String> amr,
                       String sessionId);

    /**
     * リフレッシュトークンを発行する。
     *
     * @param userId     対象ユーザーID（sub）
     * @param refreshId  リフレッシュトークンID（jti）。ローテーションや失効判定に使用
     * @param issuedAt   発行時刻（iat）
     * @param ttl        有効期間
     * @return 署名済みリフレッシュトークン（JWT）
     */
    String issueRefresh(String userId,
                        String refreshId,
                        Instant issuedAt,
                        Duration ttl);

    /**
     * リフレッシュトークンを解析し、必要最小限の情報を取り出す。
     * sub → userId、jti → refreshId
     *
     * @param refreshToken 署名済みリフレッシュトークン
     * @return 解析結果
     * @throws IllegalArgumentException 不正・期限切れ等
     */
    RefreshParseResult parseRefresh(String refreshToken);

    /**
     * リフレッシュトークン解析結果の値オブジェクト。
     */
    record RefreshParseResult(String userId, String refreshId) {}
}

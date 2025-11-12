package com.sansa.auth.service.port.impl;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.sansa.auth.util.TokenIssuer;

/**
 * TokenIssuer のデフォルト実装。
 *
 * 注意:
 * - 本実装はプロジェクト既存の JWT 実装（鍵やアルゴリズム、クレーム設計）に合わせて
 *   内部の符号化/復号処理を実装する前提。
 * - ここではコンパイルエラー解消に必要なシグネチャを満たし、最小限の TODO を残す。
 */
public class TokenIssuerImpl implements TokenIssuer {

    private final Clock clock;

    /**
     * 例示コンストラクタ。実際は鍵やアルゴリズム設定も受け取ることが多い。
     */
    public TokenIssuerImpl(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public String issueAccessToken(String userId, int tokenVersion,
                                   Instant issuedAt, Duration ttl,
                                   List<String> amr, String sessionId) {
        // TODO: 既存の JWT 生成ロジックに差し替え
        // - sub=userId, tv=tokenVersion, iat=issuedAt, exp=issuedAt+ttl, amr, sid=sessionId
        // - iss, aud, nbf 等の付与はプロジェクト規約に従う
        return "ACCESS.TOKEN.PLACEHOLDER";
    }

    @Override
    public String issueRefreshToken(String userId, String refreshId,
                                    Instant issuedAt, Duration ttl) {
        // TODO: 既存の JWT 生成ロジックに差し替え
        // - sub=userId, jti=refreshId, iat/exp など
        return "REFRESH.TOKEN.PLACEHOLDER";
    }

    @Override
    public String parseAccessSubject(String jwt) {
        // TODO: 既存パーサで sub を抽出
        return "user-id-placeholder";
    }

    @Override
    public int parseAccessTokenVersion(String jwt) {
        // TODO: 既存パーサで tv を抽出
        return 0;
    }

    @Override
    public RefreshParseResult parseRefresh(String jwt) {
        // ★修正点: 非修飾名ではなく TokenIssuer.RefreshParseResult で返す
        // ただし実装クラス内ではインターフェース継承により単純名でも解決可。
        // TODO: 既存パーサで sub と jti を抽出して返す
        return new RefreshParseResult("user-id-placeholder", "refresh-id-placeholder");
    }

    @Override
    public String newRefreshId() {
        // TODO: UUID v4 など衝突しにくい ID を返す
        return java.util.UUID.randomUUID().toString();
    }
}

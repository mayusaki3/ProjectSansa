package com.sansa.auth.service.port.impl;

import com.sansa.auth.service.port.TokenFacade;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * TokenFacade の暫定実装。
 * いったんアプリを起動するためのダミー実装です。
 * トークン形式や TTL は後で本実装と差し替える前提。
 */
@Component
public class TokenFacadeImpl implements TokenFacade {

    @Override
    public Tokens issueTokens(String userId,
                              int tokenVersion,
                              Instant now,
                              Duration accessTtl,
                              Duration refreshTtl,
                              String sessionId,
                              List<String> amr) {

        Tokens tokens = new Tokens();

        // TODO: 本実装では JWT など適切な形式に置き換える
        tokens.accessToken = "access-" + UUID.randomUUID();
        tokens.refreshToken = "refresh-" + UUID.randomUUID();

        // セッションIDが指定されていなければ新規発行
        tokens.sessionId = (sessionId != null) ? sessionId : UUID.randomUUID().toString();

        return tokens;
    }

    @Override
    public RotateResult rotateRefreshToken(String refreshToken, Instant now) {
        RotateResult result = new RotateResult();

        // TODO: refreshToken の検証やブラックリスト登録などを本実装で行う
        result.newRefreshToken = "refresh-" + UUID.randomUUID();
        result.rotatedAt = (now != null) ? now : Instant.now();

        return result;
    }

    @Override
    public void blacklistRefreshToken(String refreshToken, Instant now) {
        // TODO: 本実装ではブラックリストを永続化ストアに登録
        // いったんは何もしないダミー実装
    }

    @Override
    public Tokens issueAfterAuth(String userId, List<String> amr) {
        // TODO: TTL は AuthServiceImpl 側の設定と揃える
        Instant now = Instant.now();
        Duration defaultAccessTtl = Duration.ofMinutes(15);
        Duration defaultRefreshTtl = Duration.ofDays(7);

        return issueTokens(
                userId,
                0,                 // tokenVersion は暫定で 0
                now,
                defaultAccessTtl,
                defaultRefreshTtl,
                null,              // 新規セッション扱い
                amr
        );
    }
}

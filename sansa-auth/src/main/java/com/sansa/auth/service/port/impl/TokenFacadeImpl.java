package com.sansa.auth.service.port.impl;

import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.service.port.TokenIssuer;
import com.sansa.auth.store.Store;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * TokenFacade 実装。Store と TokenIssuer を仲介する。
 * 以前のエラーは createSession の引数や List→int などのミスマッチが原因。
 * 本実装ではシグネチャを揃え、セッション生成→トークン発行→DTO組立の順で処理する。
 */
public class TokenFacadeImpl implements TokenFacade {

    private final Store store;
    private final TokenIssuer issuer;

    public TokenFacadeImpl(Store store, TokenIssuer issuer) {
        this.store = Objects.requireNonNull(store);
        this.issuer = Objects.requireNonNull(issuer);
    }

    @Override
    public Tokens issueTokens(String userId,
                              int tokenVersion,
                              Instant now,
                              Duration accessTtl,
                              Duration refreshTtl,
                              String sessionId,
                              List<String> amr) {

        // セッションIDが未指定なら新規採番
        String sid = (sessionId != null && !sessionId.isBlank()) ? sessionId : UUID.randomUUID().toString();

        // セッション作成（Storeの4引数版に統一）
        store.createSession(userId, sid, String.join(" ", amr), now);

        // 実トークン発行
        String at = issuer.issueAccessToken(userId, tokenVersion, now, accessTtl, amr);
        String rt = issuer.issueRefreshToken(userId, tokenVersion, now, refreshTtl);

        // 返却
        Tokens t = new Tokens();
        t.accessToken = at;
        t.refreshToken = rt;
        t.sessionId = sid;
        return t;
    }

    @Override
    public RotateResult rotateRefreshToken(String refreshToken, Instant now) {
        // 実装例: 古いRTをブラックリストへ、再発行
        blacklistRefreshToken(refreshToken, now);
        RotateResult r = new RotateResult();
        r.newRefreshToken = UUID.randomUUID().toString(); // 実装では issuer を使う
        r.rotatedAt = now;
        return r;
    }

    @Override
    public void blacklistRefreshToken(String refreshToken, Instant now) {
        // 実装例: ブラックリスト保管が必要なら Store や別Repoへ登録する
        // ここではNOP
    }

    @Override
    public Tokens issueAfterAuth(String userId, List<String> amr) {
        Instant now = Instant.now();
        // 追加AMRを含めて短期AT再発行。RTは再利用想定なら再発行しない設計もあり得る。
        return issueTokens(userId, store.getTokenVersion(userId), now,
                Duration.ofMinutes(15), Duration.ofDays(7),
                null, amr);
    }
}

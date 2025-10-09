package com.sansa.auth.util;

import com.sansa.auth.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * UT-05-001..006
 * 仕様根拠: 05_Util_トークン署名・TTL・検証.md
 */
class TokenUtilTest {

    @Test @DisplayName("UT-05-001: 署名検証成功（正しい鍵・alg）")
    void verify_signature_ok() {
        var ts = new TokenService();
        var pair = ts.issuePair("user-1", 10, 3600);
        assertThat(ts.verify(pair.getAccessToken())).isTrue();
    }

    @Test @DisplayName("UT-05-003: TTL 満了で無効")
    void token_expired_401() {
        var ts = new TokenService();
        var pair = ts.issuePair("user-1", 10, -1); // 既に期限切れの exp
        assertThatThrownBy(() -> ts.requireValid(pair.getAccessToken()))
                .hasMessageContaining("token_expired");
    }

    // tv 埋め込み/refresh再利用検知は雛形
    @Test @DisplayName("UT-05-004: token_version 埋め込み→logout_all後は不一致で401")
    void tv_mismatch_after_logout_all() { /* TODO */ }

    @Test @DisplayName("UT-05-006: /token/reused 検知後は旧AT/RT=401")
    void reused_after_detected() { /* TODO */ }
}

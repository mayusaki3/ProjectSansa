package com.sansa.auth.util;

import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UT-02: TOTP の相互運用確認
 *
 * 目的: - RFC 6238 準拠の TOTP 生成をテスト側で再現し、verify ロジックと突き合わせ可能にする -
 * 実装差異による桁・桁切り捨てのズレを早期に検知
 *
 * 注意: - 本テストはユーティリティ層の「仕様確認用」。Store や Service に依存しない。
 */
class MfaUtilTest {

    /**
     * テスト用に TOTP を 30 秒ステップで生成する簡易関数（SHA1・桁=6）
     */
    static String totp(String base32Secret, long timeSeconds, int step, int digits) throws Exception {
        Base32 b32 = new Base32();
        byte[] key = b32.decode(base32Secret.toUpperCase());
        long counter = timeSeconds / step;

        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(counter);
        byte[] msg = buf.array();

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key, "HmacSHA1"));
        byte[] hash = mac.doFinal(msg);

        int offset = hash[hash.length - 1] & 0xF;
        int binary = ((hash[offset] & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                | (hash[offset + 3] & 0xFF);
        int otp = binary % (int) Math.pow(10, digits);
        return String.format("%0" + digits + "d", otp);
    }

    @Test
    @DisplayName("TOTP の生成フォーマットは 6 桁ゼロパディング")
    void totp_format() throws Exception {
        String secret = "JBSWY3DPEHPK3PXP"; // "Hello!" の例でよく使われる Base32
        String code = totp(secret, Instant.now().getEpochSecond(), 30, 6);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
    }

    @Test
    @DisplayName("時刻を変えればコードも変わる（同一時刻は同じ）")
    void totp_time_changes() throws Exception {
        String secret = "JBSWY3DPEHPK3PXP";
        long t = 1_700_000_000L;
        String c1 = totp(secret, t, 30, 6);
        String c2 = totp(secret, t, 30, 6);
        String c3 = totp(secret, t + 31, 30, 6);

        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
    }

    @Test
    @DisplayName("ランダムシークレットでも生成できる")
    void randomSecret() throws Exception {
        byte[] raw = new byte[20]; // 160bit
        new SecureRandom().nextBytes(raw);
        String b32 = new Base32().encodeToString(raw);
        String code = totp(b32, Instant.now().getEpochSecond(), 30, 6);
        assertNotNull(code);
    }
}

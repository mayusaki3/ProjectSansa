package com.sansa.auth.config;

import org.apache.commons.codec.binary.Base32;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sansa.auth.service.impl.MfaServiceImpl;
import com.sansa.auth.util.TokenIssuer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

/**
 * MFA(TOTP) に必要な軽量ユーティリティを DI で差し込むための配線クラス。
 * - ライブラリ本体（Google Authenticator 互換）には依存せず、最小限の実装を提供
 * - MfaServiceImpl.TotpLib のシグネチャに厳密一致
 */
@Configuration
public class MfaWiringConfig {

    private static final int SECRET_BYTES = 20;      // 推奨 20bytes
    private static final int DIGITS = 6;             // 6 桁コード
    private static final int PERIOD = 30;            // 30 秒ステップ
    private static final String HMAC_ALG = "HmacSHA1";// Google Authenticator 既定

    @Bean
    public MfaServiceImpl.TotpLib totpLib() {
        return new MfaServiceImpl.TotpLib() {

            /** シークレットの Base32 文字列を生成 */
            @Override
            public String generateSecret() {
                byte[] raw = new byte[SECRET_BYTES];
                new SecureRandom().nextBytes(raw);
                return new Base32().encodeToString(raw).replace("=", "");
            }

            /** otpauth:// URI を発行（表示名の順序に注意：issuer, account, secret） */
            @Override
            public String buildOtpAuthUri(String issuer, String account, String secret) {
                String label = percent(issuer) + ":" + percent(account);
                String query = "secret=" + percent(secret)
                        + "&issuer=" + percent(issuer)
                        + "&algorithm=SHA1&digits=" + DIGITS + "&period=" + PERIOD;
                return "otpauth://totp/" + label + "?" + query;
            }

            /** 現在時刻に対する 6 桁コードを生成 */
            public String currentCode(String base32Secret) {
                long counter = Instant.now().getEpochSecond() / PERIOD;
                return hotp(base32Secret, counter, DIGITS);
            }

            /** 与えられたコードが、許容ウィンドウ内で正しいかを検証 */
            @Override
            public boolean verify(String base32Secret, String code) {
                // 誤差許容（例：±1 ステップ）
                long now = Instant.now().getEpochSecond() / PERIOD;
                if (constantTimeEq(hotp(base32Secret, now - 1, DIGITS), code)) return true;
                if (constantTimeEq(hotp(base32Secret, now, DIGITS), code)) return true;
                if (constantTimeEq(hotp(base32Secret, now + 1, DIGITS), code)) return true;
                return false;
            }

            // ===== ここから下は TotpLib には無いユーティリティ。@Override を付けない =====

            private String hotp(String base32Secret, long counter, int digits) {
                byte[] key = new Base32().decode(base32Secret);
                byte[] msg = ByteBuffer.allocate(8).putLong(counter).array();
                byte[] hash = hmacSha1(key, msg);
                int offset = hash[hash.length - 1] & 0x0f;
                int binCode = ((hash[offset] & 0x7f) << 24)
                        | ((hash[offset + 1] & 0xff) << 16)
                        | ((hash[offset + 2] & 0xff) << 8)
                        | (hash[offset + 3] & 0xff);
                int otp = binCode % (int) Math.pow(10, digits);
                return String.format("%0" + digits + "d", otp);
            }

            private byte[] hmacSha1(byte[] key, byte[] msg) {
                try {
                    Mac mac = Mac.getInstance(HMAC_ALG);
                    mac.init(new SecretKeySpec(key, HMAC_ALG));
                    return mac.doFinal(msg);
                } catch (Exception e) {
                    throw new IllegalStateException("HMAC calculation failed", e);
                }
            }

            private String percent(String s) {
                return URLEncoder.encode(s, StandardCharsets.UTF_8);
            }

            private boolean constantTimeEq(String a, String b) {
                if (a == null || b == null || a.length() != b.length()) return false;
                int r = 0;
                for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
                return r == 0;
            }
        };
    }

    @Bean
    public MfaServiceImpl.Mailer mailer() {
        // ★インターフェースのメソッド名・引数に合わせて中身だけ調整してください
        return new MfaServiceImpl.Mailer() {
            @Override
            public void sendMfaCode(String to, String subject) { // ←例
                // 本番はSMTP等に差し替え。ここはダミー送信（ログ出力など）
                String body = "";
                System.out.printf("[MFA-EMAIL] to=%s, subject=%s, body=%s%n", to, subject, body);
            }
        };
    }
}

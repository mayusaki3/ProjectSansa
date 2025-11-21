package com.sansa.auth.dto.mfa;

/** TOTP 初期化レスポンス（UI で QR 化する前提。既定パラメータも返す） */
public class MfaTotpInitResponse {
    private final String secret;
    private final String algorithm; // 例: "SHA1"
    private final int digits;      // 例: 6
    private final int period;      // 例: 30

    public MfaTotpInitResponse(String secret) {
        this(secret, "SHA1", 6, 30);
    }
    public MfaTotpInitResponse(String secret, String algorithm, int digits, int period) {
        this.secret = secret; this.algorithm = algorithm; this.digits = digits; this.period = period;
    }
    public String getSecret() { return secret; }
    public String getAlgorithm() { return algorithm; }
    public int getDigits() { return digits; }
    public int getPeriod() { return period; }
}

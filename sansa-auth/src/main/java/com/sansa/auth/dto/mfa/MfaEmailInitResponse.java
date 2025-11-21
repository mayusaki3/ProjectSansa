package com.sansa.auth.dto.mfa;

/** Email MFA 初期化レスポンス（送信可否） */
public class MfaEmailInitResponse {
    private final boolean sent;
    public MfaEmailInitResponse(boolean sent) { this.sent = sent; }
    public boolean isSent() { return sent; }
}

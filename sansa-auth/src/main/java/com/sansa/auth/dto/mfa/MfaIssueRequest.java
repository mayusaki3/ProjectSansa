package com.sansa.auth.dto.mfa;

/** MFA 成功後のトークン発行リクエスト */
public class MfaIssueRequest {
    private final String accountId;
    private final String sessionId;
    private final String accessJti;
    private final String refreshJti;
    private final String userAgent;
    private final String ipAddress;

    public MfaIssueRequest(String accountId, String sessionId, String accessJti, String refreshJti,
                           String userAgent, String ipAddress) {
        this.accountId = accountId; this.sessionId = sessionId;
        this.accessJti = accessJti; this.refreshJti = refreshJti;
        this.userAgent = userAgent; this.ipAddress = ipAddress;
    }
    public String getAccountId() { return accountId; }
    public String getSessionId() { return sessionId; }
    public String getAccessJti() { return accessJti; }
    public String getRefreshJti() { return refreshJti; }
    public String getUserAgent() { return userAgent; }
    public String getIpAddress() { return ipAddress; }
}

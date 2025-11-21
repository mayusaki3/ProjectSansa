package com.sansa.auth.dto.mfa;

import com.sansa.auth.dto.login.LoginTokens;

/** MFA 成功後のトークン発行レスポンス */
public class MfaIssueResponse {
    private final LoginTokens tokens;
    public MfaIssueResponse(LoginTokens tokens) { this.tokens = tokens; }
    public LoginTokens getTokens() { return tokens; }
}

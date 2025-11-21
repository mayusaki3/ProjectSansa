package com.sansa.auth.dto.mfa;

import java.util.List;

/** Recovery コード発行レスポンス */
public class MfaRecoveryInitResponse {
    private final List<String> codes;
    public MfaRecoveryInitResponse(List<String> codes) { this.codes = codes; }
    public List<String> getCodes() { return codes; }
}

package com.sansa.auth.service.impl;

import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.mfa.*;
import com.sansa.auth.service.MfaService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.store.Store;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * MFA サービス実装
 *  - TOTP / Email / Recovery を Store に委譲
 *  - 認証成功後のトークン発行は TokenFacade に委譲
 */
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {

    private final Store store;
    private final TokenFacade tokenFacade;
    private final SessionService sessionService;

    @Override
    public MfaTotpInitResponse totpInit(MfaTotpInitRequest req) {
        String secret = store.totpIssueSecret(req.getAccountId());
        return new MfaTotpInitResponse(secret);
    }

    @Override
    public MfaTotpVerifyResponse totpVerify(MfaTotpVerifyRequest req) {
        boolean ok = store.totpVerify(req.getAccountId(), req.getCode());
        if (ok) store.totpMarkEnabled(req.getAccountId());
        return new MfaTotpVerifyResponse(ok);
    }

    @Override
    public MfaEmailInitResponse emailInit(MfaEmailInitRequest req) {
        String code = store.emailMfaIssueCode(req.getAccountId(), 6);
        return new MfaEmailInitResponse(code != null);
    }

    @Override
    public MfaEmailVerifyResponse emailVerify(MfaEmailVerifyRequest req) {
        boolean ok = store.emailMfaVerifyCode(req.getAccountId(), req.getCode());
        return new MfaEmailVerifyResponse(ok);
    }

    @Override
    public MfaRecoveryInitResponse recoveryInit(MfaRecoveryInitRequest req) {
        List<String> codes = store.recoveryIssueCodes(req.getAccountId(), 8);
        return new MfaRecoveryInitResponse(codes);
    }

    @Override
    public MfaRecoveryVerifyResponse recoveryVerify(MfaRecoveryVerifyRequest req) {
        boolean ok = store.recoveryConsumeCode(req.getAccountId(), req.getCode());
        return new MfaRecoveryVerifyResponse(ok);
    }

    @Override
    public MfaIssueResponse issueAfterMfa(MfaIssueRequest req) {
        TokenFacade.Tokens t = tokenFacade.issueAfterMfa(req.getAccountId(), req.getSessionId(), req.getAccessJti(), req.getRefreshJti());
        sessionService.upsertSession(
                req.getAccountId(),
                req.getSessionId(),
                Instant.now(),
                t.refreshExpiresAt(),
                req.getUserAgent(),
                req.getIpAddress()
        );
        LoginTokens tokens = LoginTokens.builder()
                .accessToken(t.accessToken())
                .refreshToken(t.refreshToken())
                .build();
        return new MfaIssueResponse(tokens);
    }
}

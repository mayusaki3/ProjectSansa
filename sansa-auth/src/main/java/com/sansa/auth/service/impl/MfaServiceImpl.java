package com.sansa.auth.service.impl;

import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.mfa.*;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.service.MfaService;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.service.port.TokenFacade.TokenPair;
import com.sansa.auth.store.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * /auth/mfa 配下のユースケース実装
 * 対応仕様: 04_MFA.md
 * - TOTP:   enroll → activate → verify(→ LoginResponse)
 * - Email:  send → verify(→ LoginResponse)
 * - Recovery: issue → verify(→ LoginResponse)
 *
 * 方針:
 * - verify系は LoginResponse を返す（ログインフロー統一）。
 * - すべて amr に "mfa" を追加し、要素別に "totp" / "email" / "recovery" を付ける。
 */
@Service
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {

    private final Store store;
    private final TotpLib totp;              // TOTP 検証ライブラリ（抽象）
    private final Mailer mailer;             // メール送信（抽象）
    private final TokenFacade tokenFacade;   // 認証完了後の AT/RT/Session 発行（抽象）

    // ---- TOTP ---------------------------------------------------------------

    @Override
    public MfaTotpEnrollResponse totpEnroll()
            throws UnauthorizedException, BadRequestException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        String secret = store.issueTotpSecret(userId);
        String uri = totp.buildOtpAuthUri("Sansa", userId, secret);
        return new MfaTotpEnrollResponse(secret, uri);
    }

    @Override
    public void totpActivate(MfaTotpActivateRequest req)
            throws UnauthorizedException, BadRequestException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        String secret = store.getTotpSecret(userId).orElseThrow(() -> new BadRequestException("not enrolled"));
        boolean ok = totp.verify(secret, req.getCode());
        if (!ok) throw new BadRequestException("invalid totp code");
        store.markTotpEnabled(userId);
    }

    @Override
    public LoginResponse totpVerify(MfaTotpVerifyRequest req)
            throws UnauthorizedException, BadRequestException {
        String userId = requireChallengeUser(req.getChallengeId());
        String secret = store.getTotpSecret(userId).orElseThrow(() -> new BadRequestException("not enrolled"));
        boolean ok = totp.verify(secret, req.getCode());
        if (!ok) throw new BadRequestException("invalid totp code");
        TokenPair tokens = tokenFacade.issueAfterAuth(userId, List.of("pwd", "mfa", "totp"));
        return LoginResponse.builder()
                .authenticated(true)
                .mfaRequired(false)
                .tokens(LoginTokens(tokens.accessToken(), tokens.refreshToken()))
                .expiresIn(null)
                .scope(null)
                .build();
    }

    // ---- Email --------------------------------------------------------------

    @Override
    public void emailSend(MfaEmailSendRequest req)
            throws UnauthorizedException, BadRequestException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        boolean ok = store.tryConsumeRateLimit("mfaEmail:" + userId, 5, 10);
        if (!ok) return; // サイレントスロットル
        store.issueEmailMfaCode(userId, Duration.ofMinutes(5));
        mailer.sendMfaCode(userId /*→ email 解決は mailer 側 */, "Your MFA code");
    }

    @Override
    public LoginResponse emailVerify(MfaEmailVerifyRequest req)
            throws UnauthorizedException, BadRequestException {
        String userId = requireChallengeUser(req.getChallengeId());
        boolean ok = store.verifyEmailMfaCode(userId, req.getCode());
        if (!ok) throw new BadRequestException("invalid or expired email code");
        TokenPair tokens = tokenFacade.issueAfterAuth(userId, List.of("pwd", "mfa", "email"));
        return LoginResponse.builder()
                .authenticated(true)
                .mfaRequired(false)
                .tokens(LoginTokens(tokens.accessToken(), tokens.refreshToken()))
                .expiresIn(null)
                .scope(null)
                .build();
    }

    // ---- Recovery -----------------------------------------------------------

    @Override
    public MfaRecoveryIssueResponse recoveryIssue()
            throws UnauthorizedException, BadRequestException {
        String userId = CurrentRequestContext.getUserIdOrThrow();
        List<String> codes = store.issueRecoveryCodes(userId, 10);
        return new MfaRecoveryIssueResponse(codes);
    }

    @Override
    public LoginResponse recoveryVerify(MfaRecoveryVerifyRequest req)
            throws UnauthorizedException, BadRequestException {
        String userId = requireChallengeUser(req.getChallengeId());
        boolean ok = store.consumeRecoveryCode(userId, req.getCode());
        if (!ok) throw new BadRequestException("invalid recovery code");
        TokenPair tokens = tokenFacade.issueAfterAuth(userId, List.of("pwd", "mfa", "recovery"));
        return LoginResponse.builder()
                .authenticated(true)
                .mfaRequired(false)
                .tokens(LoginTokens().builder()
                        .accessToken(tokens.accessToken())
                        .refreshToken(tokens.refreshToken()))
                .expiresIn(null)
                .scope(null)
                .build();
    }

    // ---- 補助 ---------------------------------------------------------------

    private String requireChallengeUser(String challengeId) throws BadRequestException {
        // 実装メモ：challengeId → userId の関連はセッション or 一時テーブルで管理
        // ここではサンプルとして現在ユーザーを返す
        return CurrentRequestContext.getUserIdOrThrow();
    }

    public interface TotpLib {
        String generateSecret();
        String buildOtpAuthUri(String issuer, String accountLabel, String secret);
        boolean verify(String secret, String code);
    }

    public interface Mailer {
        void sendMfaCode(String userId, String subject);
    }

    public static final class CurrentRequestContext {
        public static String getUserIdOrThrow() { throw new UnsupportedOperationException(); }
    }
}

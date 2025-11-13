package com.sansa.auth.service.impl;

import com.sansa.auth.dto.token.LoginTokens;
import com.sansa.auth.service.MfaService;
import com.sansa.auth.service.port.SessionService;
import com.sansa.auth.store.Store;
import com.sansa.auth.util.PasswordHasher;
import com.sansa.auth.facade.TokenFacade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * MFA（多要素認証）を管理するサービス実装。
 *
 * 主に以下の責務を持つ：
 *  - TOTPシークレット発行・有効化
 *  - メールMFAコードの発行・検証
 *  - リカバリーコードの発行・消費
 *  - MFA完了後のトークン発行
 *
 * @author Sansa
 */
@Service
@Transactional
public class MfaServiceImpl implements MfaService {

    private final Store store;
    private final TokenFacade tokenFacade;
    private final SessionService sessionService;

    public MfaServiceImpl(Store store, TokenFacade tokenFacade, SessionService sessionService) {
        this.store = store;
        this.tokenFacade = tokenFacade;
        this.sessionService = sessionService;
    }

    // ==========================================================
    // TOTP（ワンタイムパスワード）関連
    // ==========================================================

    /**
     * 新しいTOTPシークレットを発行する。
     *
     * @param userId 対象ユーザーID
     * @return 発行されたシークレットキー
     */
    @Override
    public String issueTotpSecret(String userId) {
        return store.totpIssueSecret(userId);
    }

    /**
     * TOTPシークレットを取得する。
     *
     * @param userId 対象ユーザーID
     * @return シークレット（登録済みの場合）
     */
    @Override
    public String getTotpSecret(String userId) {
        return store.totpGetSecret(userId);
    }

    /**
     * TOTP認証を有効化する。
     *
     * @param userId 対象ユーザーID
     */
    @Override
    public void enableTotp(String userId) {
        store.totpMarkEnabled(userId);
    }

    // ==========================================================
    // メールMFA関連
    // ==========================================================

    /**
     * メール認証コードを発行する。
     *
     * @param userId 対象ユーザーID
     * @param ttlSec 有効期限（秒）
     * @return 発行されたコード
     */
    @Override
    public String issueEmailMfaCode(String userId, int ttlSec) {
        return store.emailMfaIssueCode(userId, ttlSec);
    }

    /**
     * メール認証コードを検証する。
     *
     * @param userId 対象ユーザーID
     * @param code 検証対象コード
     * @return 成功時true
     */
    @Override
    public boolean verifyEmailMfaCode(String userId, String code) {
        return store.emailMfaVerifyCode(userId, code);
    }

    // ==========================================================
    // リカバリーコード関連
    // ==========================================================

    /**
     * リカバリーコードを複数発行する。
     *
     * @param userId 対象ユーザーID
     * @param count 発行数
     * @return 発行されたコードリスト
     */
    @Override
    public List<String> issueRecoveryCodes(String userId, int count) {
        return store.recoveryIssueCodes(userId, count);
    }

    /**
     * リカバリーコードを消費する。
     *
     * @param userId 対象ユーザーID
     * @param code 消費対象コード
     * @return 消費成功時true
     */
    @Override
    public boolean consumeRecoveryCode(String userId, String code) {
        return store.recoveryConsumeCode(userId, code);
    }

    // ==========================================================
    // MFA完了後のトークン発行
    // ==========================================================

    /**
     * MFA成功後にログイントークンを発行する。
     *
     * @param userId 対象ユーザーID
     * @param sessionId セッションID
     * @param ipAddress 接続元IP
     * @param userAgent ユーザーエージェント
     * @return 新しいLoginTokens
     */
    @Override
    public LoginTokens issueTokensAfterMfa(String userId, String sessionId, String ipAddress, String userAgent) {
        TokenFacade.Tokens t = tokenFacade.issueAfterMfa(userId, sessionId, ipAddress, userAgent);
        return LoginTokens.builder()
                .accessToken(t.accessToken())
                .refreshToken(t.refreshToken())
                .build();
    }

    // ==========================================================
    // セッション管理（オプション）
    // ==========================================================

    /**
     * MFA成功時にセッション情報を更新する。
     * セッションが存在しない場合は新規作成。
     *
     * @param userId 対象ユーザーID
     * @param sessionId セッションID
     * @param ipAddress IPアドレス
     * @param userAgent ユーザーエージェント
     */
    @Override
    public void updateSessionAfterMfa(String userId, String sessionId, String ipAddress, String userAgent) {
        Instant now = Instant.now();
        sessionService.upsertSession(userId, sessionId, now, now.plusSeconds(86400), ipAddress, userAgent);
    }
}

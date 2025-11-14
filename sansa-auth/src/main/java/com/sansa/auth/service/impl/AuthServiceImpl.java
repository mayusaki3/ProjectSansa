package com.sansa.auth.service.impl;

import com.sansa.auth.dto.auth.PreRegisterRequest;
import com.sansa.auth.dto.auth.PreRegisterResponse;
import com.sansa.auth.dto.auth.RegisterRequest;
import com.sansa.auth.dto.auth.RegisterResponse;
import com.sansa.auth.dto.auth.VerifyEmailRequest;
import com.sansa.auth.dto.auth.VerifyEmailResponse;
import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.mail.MailComposer;
import com.sansa.auth.mail.MailService;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.port.PasswordPort;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.store.Store;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * AuthService 実装（PasswordPort 依存）
 * 役割:
 *  - 事前登録/メール認証/本登録
 *  - ログイン/ログアウト
 * 注意:
 *  - トランザクションは spring-tx により注釈可能だが、現状は最小限ロジックで完結。
 */
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Store store;
    private final TokenFacade tokenFacade;
    private final SessionService sessionService;
    private final PasswordPort passwordPort;
    private final MailService mailService;
    private final MailComposer mailComposer;

    @Override
    public PreRegisterResponse preRegister(PreRegisterRequest req) {
        // 省略: 入力検証は Controller/Validation 層で実施想定
        String preRegId = store.createPreRegistration(req.getEmail(), req.getLanguage());
        String code = store.issueEmailVerificationCode(preRegId);
        mailService.send(mailComposer.composeVerifyEmail(req.getEmail(), code, req.getLanguage()));
        return new PreRegisterResponse(preRegId);
    }

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest req) {
        boolean ok = store.verifyEmailCode(req.getPreRegId(), req.getCode());
        return new VerifyEmailResponse(ok);
    }

    @Override
    public RegisterResponse register(RegisterRequest req) {
        // 事前チェック
        String preRegId = req.getPreRegId();
        if (!store.isPreRegistrationVerified(preRegId)) {
            return new RegisterResponse(false);
        }
        // パスワードハッシュ化（PasswordPort 経由）
        String hash = passwordPort.encode(req.getPassword());
        String accountId = store.createUserFromPreRegistration(preRegId, hash, req.getLanguage());
        return new RegisterResponse(accountId != null);
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        Optional<Store.User> user = store.findUserByEmail(req.getEmail());
        if (user.isEmpty()) return new LoginResponse(false, null);

        // パスワード照合
        if (!passwordPort.matches(req.getPassword(), user.get().getPasswordHash())) {
            return new LoginResponse(false, null);
        }

        // セッションIDは TokenFacade から払い出し
        String sessionId = tokenFacade.generateSessionId();
        TokenFacade.Tokens t = tokenFacade.issue(user.get().getAccountId(), sessionId, List.of("user"));
        sessionService.upsertSession(
                user.get().getAccountId(),
                sessionId,
                Instant.now(),
                t.refreshExpiresAt(),
                req.getUserAgent(),
                req.getIpAddress()
        );

        LoginTokens tokens = LoginTokens.builder()
                .accessToken(t.accessToken())
                .refreshToken(t.refreshToken())
                .build();
        return new LoginResponse(true, tokens);
    }

    @Override
    public LogoutResponse logout(String accountId, String sessionId) {
        sessionService.logoutBySessionId(accountId, sessionId);
        return new LogoutResponse();
    }

    @Override
    public LogoutResponse logoutAll(String accountId) {
        store.incrementTokenVersion(accountId); // 全トークン失効
        sessionService.deleteAllSessions(accountId);
        return new LogoutResponse();
    }

    @Override
    public LoginResponse refresh(String refreshToken) {
        TokenFacade.RefreshParseResult r = tokenFacade.refresh(refreshToken);
        // 新アクセストークンのみ再発行（セッションは維持）
        TokenFacade.Tokens t = tokenFacade.issueAfterMfa(r.userId(), r.sessionId(), r.accessJti(), r.refreshJti());
        LoginTokens tokens = LoginTokens.builder()
                .accessToken(t.accessToken())
                .refreshToken(t.refreshToken())
                .build();
        return new LoginResponse(true, tokens);
    }
}

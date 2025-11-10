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
import com.sansa.auth.dto.login.TokenRefreshRequest;
import com.sansa.auth.dto.login.TokenRefreshResponse;
import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.port.CurrentUserPort;
import com.sansa.auth.service.port.PasswordPort;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.store.Store;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * AuthService の本番実装。
 * コアロジックのみ。Spring 等のフレームワーク依存は持たない。
 *
 * 参照仕様:
 * - 01_ユーザー登録.md
 * - 02_ログイン.md
 * - 05_セッション管理.md（/auth 配下）
 */
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // 設定値はとりあえず固定。必要ならコンストラクタ引数化。
    private static final Duration PRE_REG_TTL = Duration.ofMinutes(15);
    private static final Duration REGISTER_GRACE_TTL = Duration.ofMinutes(30);
    private static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    private final Store store;
    private final PasswordPort passwordPort;
    private final TokenFacade tokenFacade;
    private final SessionService sessionService;
    private final CurrentUserPort currentUserPort;

    // ===== 01_ユーザー登録 =====

    @Override
    public PreRegisterResponse preRegister(PreRegisterRequest req) {
        Instant now = Instant.now();

        String email = req.getEmail();
        if (isBlank(email)) {
            throw new BadRequestException("email is required");
        }

        // すでに登録済みメールなら何もせず成功風レスポンス（仕様により調整）
        if (store.findUserByEmail(email).isPresent()) {
            return PreRegisterResponse.builder()
                    .success(true)
                    .throttleMs(0L)
                    .build();
        }

        Store.PreReg pre = store.issuePreRegCode(email, PRE_REG_TTL, now);

        // throttle が効いた場合は success=false + throttleMs
        if (pre.getThrottleMs() != null && pre.getThrottleMs() > 0) {
            return PreRegisterResponse.builder()
                    .success(false)
                    .throttleMs(pre.getThrottleMs())
                    .build();
        }

        // メール送信は EmailPort 側で行う想定。本実装では発行まで。
        return PreRegisterResponse.builder()
                .success(true)
                .throttleMs(0L)
                .build();
    }

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest req)
            throws BadRequestException, NotFoundException {

        Instant now = Instant.now();

        if (isBlank(req.getEmail()) || isBlank(req.getCode())) {
            throw new BadRequestException("email and code are required");
        }

        Optional<Store.PreReg> opt =
                store.consumePreRegCode(req.getEmail(), req.getCode(), now);

        Store.PreReg pre = opt.orElseThrow(
                () -> new NotFoundException("invalid or expired code"));

        if (pre.isConsumed() || now.isAfter(pre.getExpiresAt())) {
            throw new NotFoundException("invalid or expired code");
        }

        // preRegId を払い出し、以降 register で使用
        return VerifyEmailResponse.builder()
                .success(true)
                .preRegId(pre.getId())
                .expiresIn(secondsUntil(pre.getExpiresAt(), now))
                .build();
    }

    @Override
    public RegisterResponse register(RegisterRequest req)
            throws BadRequestException, NotFoundException {

        Instant now = Instant.now();

        if (isBlank(req.getPreRegId())
                || isBlank(req.getAccountId())
                || isBlank(req.getEmail())
                || isBlank(req.getPassword())) {
            throw new BadRequestException("missing required fields");
        }

        Store.PreReg pre = store.findPreRegById(req.getPreRegId())
                .orElseThrow(() -> new NotFoundException("preReg not found"));

        if (!req.getEmail().equalsIgnoreCase(pre.getEmail())) {
            throw new BadRequestException("email mismatch");
        }
        if (pre.isConsumed() || now.isAfter(pre.getExpiresAt().plus(REGISTER_GRACE_TTL))) {
            throw new BadRequestException("preReg expired");
        }

        if (store.isBlockedAccountId(req.getAccountId())) {
            throw new BadRequestException("accountId is blocked");
        }
        if (store.findUserByAccountId(req.getAccountId()).isPresent()) {
            throw new BadRequestException("accountId already exists");
        }
        if (store.findUserByEmail(req.getEmail()).isPresent()) {
            throw new BadRequestException("email already exists");
        }

        String hash = passwordPort.hash(req.getPassword());

        // Store#createUser のシグネチャに合わせる
        // 期待値: userId, accountId, email, passwordHash, emailVerified
        String userId = UUID.randomUUID().toString();
        store.createUser(userId, req.getAccountId(), req.getEmail(), hash, true);

        store.markPreRegConsumed(pre.getId(), now);

        return RegisterResponse.builder()
                .success(true)
                .userId(userId)
                .build();
    }

    // ===== 02_ログイン =====

    @Override
    public LoginResponse login(LoginRequest req)
            throws UnauthorizedException, BadRequestException {

        Instant now = Instant.now();

        if (isBlank(req.getPassword())
                || (isBlank(req.getAccountId()) && isBlank(req.getEmail()))) {
            throw new BadRequestException("invalid login request");
        }

        Store.User user = findLoginUser(req)
                .orElseThrow(() -> new UnauthorizedException("invalid credentials"));

        if (!passwordPort.verify(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("invalid credentials");
        }

        int tv = store.getTokenVersion(user.getId());

        // セッション作成
        String sessionId = UUID.randomUUID().toString();
        store.createSession(
                sessionId,
                user.getId(),
                now,
                now,
                req.getUserAgent(),
                req.getIp()
        );

        // トークン発行
        TokenFacade.Tokens tokens = tokenFacade.issueTokens(
                user.getId(),
                tv,
                now,
                ACCESS_TOKEN_TTL,
                REFRESH_TOKEN_TTL,
                sessionId
        );

        SessionInfo sessionInfo = SessionInfo.builder()
                .sessionId(sessionId)
                .createdAt(now)
                .lastActiveAt(now)
                .ip(req.getIp())
                .userAgent(req.getUserAgent())
                .build();

        LoginTokens loginTokens = new LoginTokens(
                tokens.accessToken(),
                tokens.refreshToken()
        );

        return LoginResponse.builder()
                .success(true)
                .mfaRequired(false)
                .session(sessionInfo)
                .tokens(loginTokens)
                .build();
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest req)
            throws BadRequestException, UnauthorizedException {

        if (req == null || isBlank(req.getRefreshToken())) {
            throw new BadRequestException("refreshToken is required");
        }

        Instant now = Instant.now();

        TokenFacade.RotateResult result =
                tokenFacade.rotateRefreshToken(req.getRefreshToken(), now);

        if (!result.success()) {
            throw new UnauthorizedException("invalid refresh token");
        }

        TokenRefreshResponse.Tokens dtoTokens =
                new TokenRefreshResponse.Tokens(
                        result.tokens().accessToken(),
                        result.tokens().refreshToken()
                );

        return TokenRefreshResponse.builder()
                .success(true)
                .tokens(dtoTokens)
                .tv(result.tokenVersion())
                .build();
    }

    // ===== 05_セッション管理 =====

    @Override
    public SessionInfo getCurrentSession() {
        String userId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("unauthorized"));

        // 現在セッションは CurrentUserPort が持つセッションIDを使うか、
        // なければ SessionService 側の getCurrentSession 相当を利用する設計とする。
        return sessionService.getCurrentSession(userId)
                .orElseThrow(() -> new UnauthorizedException("session not found"));
    }

    @Override
    public LogoutResponse logout(LogoutRequest req)
            throws UnauthorizedException, BadRequestException {

        String userId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("unauthorized"));

        Instant now = Instant.now();

        boolean ok = false;

        // 1) sessionId 指定があればそのセッションのみ削除
        if (!isBlank(req.getSessionId())) {
            ok = sessionService.logoutBySessionId(userId, req.getSessionId());
        }
        // 2) なければ現セッションのみ削除（CurrentUserPort がセッションIDを持っている前提）
        else {
            ok = sessionService.logoutCurrentSession(userId);
        }

        // 3) refreshTokenJti 指定があればブラックリスト登録
        if (!isBlank(req.getRefreshTokenJti())) {
            // 有効期限は RT TTL を目安に now + REFRESH_TOKEN_TTL
            tokenFacade.blacklistRefreshToken(req.getRefreshTokenJti(), now.plus(REFRESH_TOKEN_TTL));
        }

        return LogoutResponse.builder()
                .success(ok)
                .build();
    }

    @Override
    public LogoutResponse logoutAll() throws UnauthorizedException {
        String userId = currentUserPort.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("unauthorized"));

        // token_version++ により既存 AT/RT を論理的に失効
        store.incrementTokenVersion(userId);
        // 必要なら物理セッションも削除
        store.deleteAllSessions(userId);

        return LogoutResponse.builder()
                .success(true)
                .build();
    }

    // ===== internal helpers =====

    private Optional<Store.User> findLoginUser(LoginRequest req) {
        if (!isBlank(req.getAccountId())) {
            Optional<Store.User> byAccount = store.findUserByAccountId(req.getAccountId());
            if (byAccount.isPresent()) return byAccount;
        }
        if (!isBlank(req.getEmail())) {
            Optional<Store.User> byEmail = store.findUserByEmail(req.getEmail());
            if (byEmail.isPresent()) return byEmail;
        }
        return Optional.empty();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static long secondsUntil(Instant expiresAt, Instant now) {
        if (expiresAt == null) return 0L;
        long seconds = Duration.between(now, expiresAt).getSeconds();
        return Math.max(seconds, 0L);
    }

    private static String domainOf(String email) {
        int at = email.lastIndexOf('@');
        if (at < 0 || at == email.length() - 1) return "";
        return email.substring(at + 1).toLowerCase(Locale.ROOT);
    }
}

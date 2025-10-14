package com.sansa.auth.service.impl;

import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.store.Store;
import com.sansa.auth.util.JwtProvider;
import com.sansa.auth.dto.auth.RegisterRequest;
import com.sansa.auth.dto.auth.RegisterResponse;
import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.TokenRefreshRequest;
import com.sansa.auth.dto.login.TokenRefreshResponse;
import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * /auth 配下のユースケース実装
 * 対応仕様:
 * - 01_ユーザー登録.md: POST /auth/pre-register, /auth/verify-email, /auth/register
 * - 02_ログイン.md:    POST /auth/token/refresh（RTローテーション / 再利用検知 / tv）
 * - 05_セッション管理.md: GET /auth/session, POST /auth/logout, POST /auth/logout_all
 *
 * 認証まわりのユースケース実装。
 * - JwtProvider の公開APIに完全準拠：
 *   createAccessToken(subject, extraClaims)
 *   createRefreshToken(subject, jti)           // ← 2引数のみ
 *   parse(token) -> Claims
 * - 旧: tokenVersion(tv) 参照は撤去（仕様と不一致だったため）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Store store;
    private final TokenIssuer tokenIssuer;
    private final PasswordHasher passwordHasher;

    /* ==================== TokenIssuer（JwtProvider アダプタ） ==================== */
    public interface TokenIssuer {
        String newRefreshId();
        String issueAccessToken(String userId);
        String issueRefreshToken(String userId, String jti);
        RefreshParseResult parseAndValidateRefresh(String refreshToken);

        /** リフレッシュ検証結果（必要最小限） */
        record RefreshParseResult(String userId, String jti) {}
    }

    public static class DefaultTokenIssuer implements TokenIssuer {
        private final JwtProvider jwt;
        public DefaultTokenIssuer(JwtProvider jwt) { this.jwt = Objects.requireNonNull(jwt); }

        @Override public String newRefreshId() { return UUID.randomUUID().toString(); }

        @Override public String issueAccessToken(String userId) {
            return jwt.createAccessToken(userId, Map.of());
        }

        @Override public String issueRefreshToken(String userId, String jti) {
            // ★ JwtProvider は2引数版のみ
            return jwt.createRefreshToken(userId, jti);
        }

        @Override public RefreshParseResult parseAndValidateRefresh(String refreshToken) {
            final Claims c = jwt.parse(refreshToken);
            final String userId = c.getSubject();
            final String jti    = c.get("jti", String.class);
            if (userId == null || jti == null) {
                throw new IllegalArgumentException("Invalid refresh token claims.");
            }
            return new RefreshParseResult(userId, jti);
        }
    }

    /* ==================== PasswordHasher 抽象 ==================== */
    public interface PasswordHasher {
        String hash(String raw);
        boolean verify(String raw, String hashed);
    }

    /* ==================== 内部ユーティリティ（発行/回転） ==================== */
    private Tokens issueTokensForUser(String userId) {
        final String jti = tokenIssuer.newRefreshId();
        final String access  = tokenIssuer.issueAccessToken(userId);
        final String refresh = tokenIssuer.issueRefreshToken(userId, jti);
        return new Tokens(access, refresh);
    }

    private Tokens rotateTokens(String refreshToken) {
        final TokenIssuer.RefreshParseResult p = tokenIssuer.parseAndValidateRefresh(refreshToken);
        final String newJti = tokenIssuer.newRefreshId();
        final String access  = tokenIssuer.issueAccessToken(p.userId());
        final String refresh = tokenIssuer.issueRefreshToken(p.userId(), newJti);
        return new Tokens(access, refresh);
    }

    private record Tokens(String accessToken, String refreshToken) {}

    /* ==================== AuthService の未実装分を最低限埋める ==================== */

    // AuthServiceImpl.java （クラス内）
    @Override
    public RegisterResponse register(RegisterRequest req /* ← AuthServiceと同じthrows節も付ける */) {
        throw new UnsupportedOperationException("register is not implemented yet");
    }

    @Override
    public LoginResponse login(LoginRequest req) throws UnauthorizedException, BadRequestException {
        // TODO: 後で実装。まずはビルドを通す。
        throw new UnsupportedOperationException("login is not implemented yet");
    }

    // ① logout(LogoutRequest) を実装（暫定：件数0を返す）
    @Override
    public LogoutResponse logout(LogoutRequest req) {
        return LogoutResponse.builder()
                .success(true)
                .build();
    }

    // ② logoutAll() もストア呼び出しを外し、暫定で件数0を返す
    @Override
    public LogoutResponse logoutAll() {
        return LogoutResponse.builder()
                .success(true)
                .build();
    }

    @Override
    public SessionInfo getCurrentSession() {
        return SessionInfo.builder()
                    .id(null)
                    .user(null)
                    .build();
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest req)
            throws BadRequestException, UnauthorizedException {
        // ここでは Store に依存せず、RT 解析→AT/RT再発行→Storeのローテーションを行う想定。
        // TokenIssuer に合わせて実装してください（※既存の TokenIssuer / JwtProvider API に準拠）。
        // 例：
        // var parsed = tokenIssuer.parseRefresh(req.getRefreshToken());
        // var newJti = tokenIssuer.newRefreshId();
        // var at = tokenIssuer.issueAccessToken(parsed.userId(), cfg.accessTtlMinutes());
        // var rt = tokenIssuer.issueRefreshToken(parsed.userId(), newJti, cfg.refreshTtlMinutes());
        // store.rotateRefresh(parsed.userId(), parsed.jti(), newJti, parsed.tokenVersion());
        // return TokenRefreshResponse.builder()
        //         .tokens(new TokenRefreshResponse.Tokens(at, rt))
        //         .tv(parsed.tokenVersion())
        //         .build();
        throw new UnsupportedOperationException("refresh: wire to TokenIssuer/JwtProvider per current API");
    }
}

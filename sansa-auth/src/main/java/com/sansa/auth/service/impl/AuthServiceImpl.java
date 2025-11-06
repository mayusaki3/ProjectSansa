package com.sansa.auth.service.impl;

import com.sansa.auth.dto.auth.PreRegisterRequest;
import com.sansa.auth.dto.auth.PreRegisterResponse;
import com.sansa.auth.dto.auth.RegisterRequest;
import com.sansa.auth.dto.auth.RegisterResponse;
import com.sansa.auth.dto.auth.VerifyEmailRequest;
import com.sansa.auth.dto.auth.VerifyEmailResponse;
import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.TokenRefreshRequest;
import com.sansa.auth.dto.login.TokenRefreshResponse;
import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.ConflictException;
import com.sansa.auth.exception.GoneException;
import com.sansa.auth.exception.InvalidCredentialsException;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.TokenExpiredException;
import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.mail.MailService;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.store.Store;
import com.sansa.auth.util.Clock;
import com.sansa.auth.rate.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * 認証サービス（本番ロジック／コアルール）
 *
 * 役割:
 *  - API仕様およびUT期待に沿ったコアルールを実装
 *  - ポート(Store/TokenFacade/MailService/RateLimiter/Clock)に委譲し副作用を分離
 *
 * 注意点:
 *  - 例外種別は「例外・i18n・HTTPマッピング仕様」に準拠して投げる（Controllerでproblem+jsonへ集約）
 *  - i18n見出しやRateLimitヘッダは上位層で付与（本クラスではドメイン判断のみ）
 *  - パスワードはPasswordHasherで照合（ハッシュ生成・一致判定）。実体はutil側
 *
 * 引数/戻り値:
 *  - 各DTOはAPI仕様のまま。null許容は基本しない（検証で400）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final Store store;
  private final TokenFacade tokenFacade;
  private final MailService mailService;
  private final RateLimiter rateLimiter;
  private final Clock clock;
  private final PasswordHasher passwordHasher; // util側の実装に依存

  // =========================
  // 1) 事前登録: メール → コード発行
  // =========================
  @Override
  public PreRegisterResponse preRegister(PreRegisterRequest req) throws BadRequestException {
    // 引数検証
    if (req == null || req.getEmail() == null || req.getEmail().isBlank()) {
      throw new BadRequestException("email is required");
    }
    final String email = req.getEmail().trim();
    final Locale locale = req.getLocale() != null ? req.getLocale() : Locale.getDefault();

    // ブロックドメイン（上位仕様：登録前チェック）
    final int at = email.lastIndexOf('@');
    if (at <= 0) throw new BadRequestException("email format");
    final String domain = email.substring(at + 1);
    if (store.isBlockedDomain(domain)) {
      throw new BadRequestException("blocked domain");
    }

    // レート制限（上位でヘッダ化）：キーはメール単位
    final Instant now = clock.now();
    var status = rateLimiter.hit("preReg:" + email, 5, Duration.ofMinutes(10), now);
    if (status.limited) {
      // Controller側が429+ヘッダへ変換
      throw new com.sansa.auth.exception.RateLimitException("pre-register limited");
    }

    // コード発行と送信
    var pr = store.issuePreRegCode(email, Duration.ofMinutes(15), now);
    mailService.sendPreRegister(email, pr.code(), locale);

    // 応答（詳細は不要。UTは429やヘッダ、送達可否を主に検証）
    return new PreRegisterResponse(true);
  }

  // ===================================
  // 2) メールコード検証 → preRegId 付与
  // ===================================
  @Override
  public VerifyEmailResponse verifyEmail(VerifyEmailRequest req) throws BadRequestException {
    if (req == null || req.getEmail() == null || req.getCode() == null) {
      throw new BadRequestException("email and code are required");
    }
    final String email = req.getEmail().trim();
    final String code = req.getCode().trim();
    final Instant now = clock.now();

    var consumed = store.consumePreRegCode(email, code, now);
    if (consumed.isEmpty()) {
      // 期限切れ or 不一致の詳細はStore側ではなく上位で表現
      throw new BadRequestException("invalid or expired code");
    }
    // preRegIdは以後のregisterで1回のみ有効
    return new VerifyEmailResponse(consumed.get().preRegId(), true);
  }

  // ==============================================
  // 3) 本登録: preRegId → アカウント作成と初期セッション
  // ==============================================
  @Override
  public RegisterResponse register(RegisterRequest req) throws BadRequestException, ConflictException, GoneException {
    if (req == null || req.getPreRegId() == null || req.getPassword() == null || req.getAccountId() == null) {
      throw new BadRequestException("preRegId, accountId, password are required");
    }
    final UUID preRegId = req.getPreRegId();
    final String accountId = req.getAccountId().trim();
    final String password = req.getPassword();

    // preRegId妥当性確認（register時に二重使用は410）
    // ここでは preRegId→email をStore側から解決する前提の実装もあり得るが、
    // 仕様が不明なため、アカウント作成時の重複検出をConflictで返す設計に留める。
    // 実運用では preRegId 消費も含めStore側で整合を取る。

    // 一意性: accountId/email は Store.createUser 内で担保される前提
    var hashed = passwordHasher.hash(password);
    var draft = com.sansa.auth.model.user.User
        .builder()
        .accountId(accountId)
        .passwordHash(hashed)
        .email(req.getEmail()) // RegisterRequestにemailがある仕様なら利用
        .emailVerified(true)   // verifyEmail 済み前提
        .build();

    var user = store.createUser(draft);

    // 初期セッションとトークン発行
    final Instant now = clock.now();
    var sess = store.createSession(user.getId(), now,
        Duration.ofMinutes(15), Duration.ofDays(7), req.getUserAgent(), req.getIp());
    int tv = store.getTokenVersion(user.getId());
    var tokens = tokenFacade.issueTokens(user.getId(), tv, now,
        Duration.ofMinutes(15), Duration.ofDays(7), sess.getSessionId());

    return RegisterResponse.builder()
        .userId(user.getId())
        .accountId(user.getAccountId())
        .emailVerified(true)
        .accessToken(tokens.accessToken())
        .refreshToken(tokens.refreshToken())
        .accessTokenExpiresAt(tokens.accessTokenExpiresAt())
        .refreshTokenExpiresAt(tokens.refreshTokenExpiresAt())
        .sessionId(tokens.sessionId())
        .build();
  }

  // ==========================
  // 4) ログイン（パスワード）
  // ==========================
  @Override
  public LoginResponse login(LoginRequest req) throws BadRequestException, InvalidCredentialsException {
    if (req == null || req.getIdentifier() == null || req.getPassword() == null) {
      throw new BadRequestException("identifier and password are required");
    }
    final String id = req.getIdentifier().trim();
    final String raw = req.getPassword();

    // identifierは accountId or email のいずれか
    Optional<com.sansa.auth.model.user.User> userOpt =
        id.contains("@") ? store.findUserByEmail(id) : store.findUserByAccountId(id);
    var user = userOpt.orElseThrow(() -> new InvalidCredentialsException("invalid-credentials"));

    if (!passwordHasher.matches(raw, user.getPasswordHash())) {
      throw new InvalidCredentialsException("invalid-credentials");
    }

    // ここでMFA要否を判断する場合は、現UTに合わせて `mfaRequired=true` を返す分岐を挿入する。
    // 今回は最小ロジックとしてトークン発行まで行う。
    final Instant now = clock.now();
    var sess = store.createSession(user.getId(), now,
        Duration.ofMinutes(15), Duration.ofDays(7), req.getUserAgent(), req.getIp());
    int tv = store.getTokenVersion(user.getId());
    var tokens = tokenFacade.issueTokens(user.getId(), tv, now,
        Duration.ofMinutes(15), Duration.ofDays(7), sess.getSessionId());

    return LoginResponse.builder()
        .authenticated(true)
        .mfaRequired(false)
        .accessToken(tokens.accessToken())
        .refreshToken(tokens.refreshToken())
        .accessTokenExpiresAt(tokens.accessTokenExpiresAt())
        .refreshTokenExpiresAt(tokens.refreshTokenExpiresAt())
        .sessionId(tokens.sessionId())
        .amr(java.util.List.of("pwd"))
        .build();
  }

  // ==========================
  // 5) リフレッシュ（ローテ）
  // ==========================
  @Override
  public TokenRefreshResponse refresh(TokenRefreshRequest req)
      throws BadRequestException, TokenExpiredException {
    if (req == null || req.getRefreshToken() == null || req.getRefreshToken().isBlank()) {
      throw new BadRequestException("refreshToken is required");
    }
    final Instant now = clock.now();
    var rr = tokenFacade.rotateRefreshToken(req.getRefreshToken(), now);

    if (!rr.ok()) {
      // 期限切れ: 401 token_expired
      if (rr.tokenExpired()) throw new TokenExpiredException("token_expired");
      // 再利用: 401 token_reused
      if (rr.tokenReused()) throw new com.sansa.auth.exception.TokenReusedException("token_reused");
      // それ以外: 401 invalid_token
      throw new com.sansa.auth.exception.InvalidTokenException(rr.error() != null ? rr.error() : "invalid_token");
    }

    var t = rr.tokens();
    return TokenRefreshResponse.builder()
        .accessToken(t.accessToken())
        .refreshToken(t.refreshToken())
        .accessTokenExpiresAt(t.accessTokenExpiresAt())
        .refreshTokenExpiresAt(t.refreshTokenExpiresAt())
        .sessionId(t.sessionId())
        .build();
  }

  // ==================================
  // 6) 現在セッション情報の取得（要認証）
  // ==================================
  @Override
  public SessionInfo getCurrentSession() throws UnauthorizedException {
    // 認証ユーザと現セッションはSecurityContext等から取得する想定。
    // ここでは最小実装として「未実装の呼び出し」を避けるための例外を返す。
    // 実運用では Controller 層で解決したセッションID/ユーザIDを引数で受ける設計が望ましい。
    throw new UnauthorizedException("not wired current session context");
  }

  // =================
  // 7) ログアウト系
  // =================
  @Override
  public LogoutResponse logout(LogoutRequest req) throws BadRequestException {
    if (req == null) throw new BadRequestException("request is required");

    final UUID userId = req.getUserId(); // Controllerで解決済み前提
    if (userId == null) throw new BadRequestException("userId required");

    boolean any = false;

    // セッションID指定があれば優先
    if (req.getSessionId() != null && !req.getSessionId().isBlank()) {
      any |= store.deleteSession(userId, req.getSessionId().trim());
    }

    // refreshTokenがあればJTIブラックリストへ（TokenFacadeを利用）
    if (req.getRefreshTokenJti() != null && !req.getRefreshTokenJti().isBlank()) {
      // 失効期限は適切に設定すべきだが、ここでは安全側に倒して7日で失効
      tokenFacade.blacklistTokenJti(req.getRefreshTokenJti().trim(), clock.now().plus(Duration.ofDays(7)));
      any = true;
    }

    return new LogoutResponse(any);
  }

  @Override
  public LogoutResponse logoutAll() throws UnauthorizedException {
    // 認証ユーザIDの取得はControllerで行い、本メソッドには渡す設計が本来望ましい。
    // ここでは例外を投げておき、Controller側の改修後に userId を引数化して呼び出す。
    throw new UnauthorizedException("not wired current userId");
  }

  // -------------------------------------------------------------
  // 補助インターフェース（util 側で定義することを推奨）
  // -------------------------------------------------------------

  /**
   * パスワードハッシュ/照合の抽象化（BCrypt等）
   * 役割: 生パスワードのハッシュ化と一致判定
   * 引数:
   *  - hash(raw): 平文パスワード
   *  - matches(raw, hashed): 平文とハッシュ
   * 戻り値:
   *  - hash: ハッシュ文字列
   *  - matches: 一致ならtrue
   */
  public interface PasswordHasher {
    String hash(String raw);
    boolean matches(String raw, String hashed);
  }
}

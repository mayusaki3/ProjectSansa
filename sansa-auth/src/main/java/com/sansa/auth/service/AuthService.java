package com.sansa.auth.service;

import com.sansa.auth.dto.auth.*;
import com.sansa.auth.dto.login.*;
import com.sansa.auth.dto.sessions.*;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.UnauthorizedException;

/**
 * 認証系ユースケース（/auth 配下）を扱うサービス。
 * 参照仕様:
 *  - 01_ユーザー登録.md（/auth/pre-register, /auth/verify-email, /auth/register） :contentReference[oaicite:0]{index=0}
 *  - 02_ログイン.md（/auth/token/refresh） :contentReference[oaicite:1]{index=1}
 *  - 05_セッション管理.md（/auth/session, /auth/logout, /auth/logout_all） :contentReference[oaicite:2]{index=2}
 */
public interface AuthService {

    // ===== 01_ユーザー登録.md =====
    /**
     * 事前登録（検証メール用コード発行/送信）。 POST /auth/pre-register
     * 仕様: 01_ユーザー登録.md 「1) POST /auth/pre-register」 :contentReference[oaicite:3]{index=3}
     * 成功: 200/202 + PreRegisterResponse
     */
    PreRegisterResponse preRegister(PreRegisterRequest req)
            throws BadRequestException;

    /**
     * 認証コード確認（preRegId払い出し）。POST /auth/verify-email
     * 仕様: 01_ユーザー登録.md 「2) POST /auth/verify-email」 :contentReference[oaicite:4]{index=4}
     * 成功: 200 + VerifyEmailResponse（preRegId, expiresIn）
     */
    VerifyEmailResponse verifyEmail(VerifyEmailRequest req)
            throws BadRequestException, NotFoundException;

    /**
     * 本登録（ユーザー作成）。POST /auth/register
     * 仕様: 01_ユーザー登録.md 「3) POST /auth/register」 :contentReference[oaicite:5]{index=5}
     * 成功: 201 + RegisterResponse
     */
    RegisterResponse register(RegisterRequest req)
            throws BadRequestException, NotFoundException;

    // ===== 02_ログイン.md =====
    /**
     * 認証（必要に応じて MFA を要求）。POST /auth/login
     * 仕様: 02_ログイン.md 「A) POST /auth/login → LoginResponse」
     *  - 成功: 200 + LoginResponse（authenticated=true, tokens, session, amr, user）
     *  - MFA要: 200 + LoginResponse（mfaRequired=true, mfa{factors, challengeId}）
     *  - 失敗: 401 + application/problem+json
     */
    LoginResponse login(LoginRequest req)
            throws UnauthorizedException, BadRequestException;

    /**
     * RTでAT/RT再発行（RTローテーション）。POST /auth/token/refresh
     * 仕様: 02_ログイン.md 「R1」 TokenRefreshRequest/Response, tv を返す :contentReference[oaicite:6]{index=6}
     * 成功: 200 + TokenRefreshResponse（accessToken, refreshToken, tv）
     */
    TokenRefreshResponse refresh(TokenRefreshRequest req)
            throws BadRequestException, UnauthorizedException;

    // ===== 05_セッション管理.md（/auth 配下の項目のみ） =====
    /**
     * 現在セッション情報の取得。GET /auth/session
     * 仕様: 05_セッション管理.md 「#1 GET /auth/session → SessionInfo」 :contentReference[oaicite:7]{index=7}
     * 成功: 200 + SessionInfo
     */
    SessionInfo getCurrentSession()
            throws UnauthorizedException;

    /**
     * ログアウト（現セッション or 指定）。POST /auth/logout
     * 仕様: 05_セッション管理.md 「#4 POST /auth/logout」LogoutRequest/Response :contentReference[oaicite:8]{index=8}
     * 成功: 200 + LogoutResponse{success}
     */
    LogoutResponse logout(LogoutRequest req)
            throws UnauthorizedException, BadRequestException;

    /**
     * 全端末ログアウト。POST /auth/logout_all
     * 仕様: 05_セッション管理.md 「#5 POST /auth/logout_all」tv++ による全失効 :contentReference[oaicite:9]{index=9}
     * 成功: 200 または 204
     */
    LogoutResponse logoutAll()
            throws UnauthorizedException;
}

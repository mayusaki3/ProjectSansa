package com.sansa.auth.controller;

import com.sansa.auth.dto.auth.*;
import com.sansa.auth.dto.login.*;
import com.sansa.auth.dto.sessions.*;
import com.sansa.auth.service.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * /auth 配下：登録・ログイン・トークン・セッション・ログアウト
 * 成功/失敗時の Content-Language と error(problem+json) は Advice/Filter 側で処理する想定。
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;
    private final TokenService tokenService;

    // 1) 事前登録
    @PostMapping("/auth/pre-register")
    public PreRegisterResponse preRegister(@Valid @RequestBody PreRegisterRequest req) {
        return authService.preRegister(req);
    }

    // 2) メール認証
    @PostMapping("/auth/verify-email")
    public VerifyEmailResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest req) {
        return authService.verifyEmail(req);
    }

    // 3) 本登録（201 Created）
    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        RegisterResponse res = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 4) ログイン（Password 経路）
    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    // R1) トークンリフレッシュ（RTローテーション／再利用検知）
    @PostMapping("/auth/token/refresh")
    public TokenRefreshResponse refresh(@Valid @RequestBody TokenRefreshRequest req) {
        return tokenService.refresh(req);
    }

    // 6) 現在セッション
    @GetMapping("/auth/session")
    public SessionInfo currentSession() {
        return sessionService.currentSession();
    }

    // 7) ログアウト（現セッションまたは引数指定）
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) LogoutRequest req) {
        sessionService.logout(req); // reqがnullなら「現セッションのみ」を想定
        return ResponseEntity.noContent().build(); // 204
    }

    // 7b) 全端末ログアウト（token_version++）
    @PostMapping("/auth/logout_all")
    public ResponseEntity<Void> logoutAll() {
        sessionService.logoutAll();
        return ResponseEntity.noContent().build(); // 204
    }
}

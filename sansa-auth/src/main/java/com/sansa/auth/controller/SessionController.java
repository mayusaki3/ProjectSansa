// src/main/java/com/sansa/auth/controller/SessionController.java
package com.sansa.auth.controller;

import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.dto.sessions.SessionsListResponse;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.SessionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * セッション管理エンドポイントのコントローラー。
 * 返却型と引数はSessionServiceと厳密一致。
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SessionController {

    private final AuthService auth;
    private final SessionService sessions;

    /** GET /auth/session */
    @GetMapping("/auth/session")
    public SessionInfo session() {
        return auth.getCurrentSession();
    }

    /** GET /sessions */
    @GetMapping("/sessions")
    public SessionsListResponse list() {
        return sessions.list();
    }

    /** DELETE /sessions/{id} -> 204 */
    @DeleteMapping("/sessions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable @NotBlank String id) {
        sessions.deleteById(id);
    }

    /** POST /auth/logout */
    @PostMapping("/auth/logout")
    public LogoutResponse logout(@RequestBody @Valid LogoutRequest req) {
        return auth.logout(req);
    }

    /** POST /auth/logout_all */
    @PostMapping("/auth/logout_all")
    public LogoutResponse logoutAll() {
        return auth.logoutAll();
    }
}

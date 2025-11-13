package com.sansa.auth.controller;

import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.service.SessionService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * セッション管理 API
 *
 * 提供エンドポイント（API仕様 05_セッション管理.md 準拠）:
 *  - GET    /sessions               : 自アカウントのセッション一覧
 *  - DELETE /sessions/{sessionId}   : セッション個別失効
 *
 * 備考:
 *  - GET /auth/session, POST /auth/logout, POST /auth/logout_all は AuthController 側の担当。
 */
@RestController
@Validated
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessions;

    /**
     * 自アカウントのセッション一覧を返す。
     * - Service層I/F: listSessions(String userId)
     * - 現在ユーザーIDは Spring Security の Principal から取得
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionInfo>> listMySessions(Principal principal) {
        final String userId = requireUserId(principal);
        final List<SessionInfo> list = sessions.listSessions(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * 指定セッションを失効させる（ログアウト扱い）。
     * - 呼び出しユーザーが所有するセッションのみ対象。
     * - Service層I/F: logoutBySessionId(String userId, String sessionId)
     * - 返却: 204 No Content
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> revokeOne(
            Principal principal,
            @PathVariable("sessionId") @NotBlank String sessionId) {
        final String userId = requireUserId(principal);
        sessions.logoutBySessionId(userId, sessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Principal から userId を必須取得。
     * - 認証が無い/匿名の場合は 401 へ（IllegalStateException を投げ、ExceptionHandler/Filter 側で 401 変換想定）。
     */
    private static String requireUserId(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new IllegalStateException("Unauthorized");
        }
        return principal.getName();
    }
}

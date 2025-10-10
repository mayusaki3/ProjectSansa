package com.sansa.auth.controller;

import com.sansa.auth.dto.sessions.SessionsListResponse;
import com.sansa.auth.exception.SessionNotFoundException;
import com.sansa.auth.service.SessionService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * /sessions 配下のセッション一覧取得＆個別失効
 *
 * - GET    /sessions            : 自アカウントの全セッション一覧
 * - DELETE /sessions/{sessionId}: 指定セッションの失効（存在しない場合 404）
 */
@Slf4j
@RestController
@RequestMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public SessionsListResponse list() {
        return sessionService.list();
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> revokeById(@PathVariable @NotBlank String sessionId)
            throws SessionNotFoundException {
        log.info("[SessionController] revokeById sessionId={}", sessionId);
        sessionService.revokeById(sessionId); // 見つからなければ SessionNotFoundException を投げる
        return ResponseEntity.noContent().build();
    }
}

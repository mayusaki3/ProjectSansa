package com.sansa.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * テスト用の Session 簡易モデル。
 *
 * 役割:
 *   - セッション一覧表示や失効の契約をテストするための最小構成。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
    private String sessionId;
    private String accountId;
    private String userAgent;
    private String clientIp;
    private long issuedAt;
    private long expiresAt;
    private List<String> scopes;
}

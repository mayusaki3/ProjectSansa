package com.sansa.auth.service.port.impl;

import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.service.port.CurrentUserPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security の Authentication から userId を取り出す実装。
 * 実際の userId の埋め込み方に応じて実装を合わせること。
 */
public class SecurityContextCurrentUserPort implements CurrentUserPort {

    @Override
    public String getCurrentUserId() throws UnauthorizedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("unauthorized");
        }
        Object principal = auth.getPrincipal();
        // 実プロジェクトの Principal 実装に合わせて userId を取り出す
        if (principal instanceof String s && !s.isBlank()) {
            return s;
        }
        throw new UnauthorizedException("unauthorized");
    }
}

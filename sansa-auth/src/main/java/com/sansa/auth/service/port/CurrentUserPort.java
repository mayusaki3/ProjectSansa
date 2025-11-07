package com.sansa.auth.service.port;

import com.sansa.auth.exception.UnauthorizedException;

/**
 * 現在認証中のユーザーIDを取得するPort。
 * 実装は Spring Security 等のインフラ層側に置く。
 */
public interface CurrentUserPort {

    /**
     * 現在の認証済みユーザーIDを返す。
     * 未認証または不正な場合は UnauthorizedException。
     */
    String getCurrentUserId() throws UnauthorizedException;
}

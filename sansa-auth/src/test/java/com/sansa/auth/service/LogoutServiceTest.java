package com.sansa.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UT-04-001..004
 * 仕様根拠: 04_Service_ログアウト・全端末無効化.md
 */
@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Test void logout_current_session_ok() { /* TODO: 現セッション失効→/auth/session=false */ }
    @Test void logout_by_rt_or_sessionId_ok() { /* TODO */ }
    @Test void logout_all_increments_tv() { /* TODO: tv++ 検証 */ }
    @Test void logout_all_idempotent() { /* TODO */ }
}

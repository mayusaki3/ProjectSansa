package com.sansa.auth.cross;

import com.sansa.auth.controller.ApiExceptionHandler;
import com.sansa.auth.controller.AuthController;
import com.sansa.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(controllers = {AuthController.class, ApiExceptionHandler.class})
class RateLimitI18nHeaderTest {
    @Mock AuthService authService;

    @Test void preRegister_429_headers_present() { /* TODO: RateLimit-*, Retry-After */ }
    @Test void mfa_email_send_429() { /* TODO */ }
    @Test void i18n_header_echo() { /* TODO */ }
    @Test void protected_without_auth_401() { /* TODO */ }
}

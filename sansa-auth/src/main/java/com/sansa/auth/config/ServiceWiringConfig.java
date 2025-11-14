package com.sansa.auth.config;

import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.MfaService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.impl.AuthServiceImpl;
import com.sansa.auth.service.impl.MfaServiceImpl;
import com.sansa.auth.service.impl.SessionServiceImpl;
import com.sansa.auth.service.port.PasswordPort;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.store.Store;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/**
 * サービス配線
 * - PasswordPort は Argon2 で実装
 */
@Configuration
public class ServiceWiringConfig {

    @Bean
    public PasswordPort passwordPort() {
        Argon2PasswordEncoder enc = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        return new PasswordPort() {
            @Override public String encode(String raw) { return enc.encode(raw); }
            @Override public boolean matches(String raw, String hashed) { return enc.matches(raw, hashed); }
        };
    }

    @Bean
    public AuthService authService(Store store, TokenFacade tokenFacade, SessionService sessionService,
                                   PasswordPort passwordPort,
                                   com.sansa.auth.mail.MailService mailService,
                                   com.sansa.auth.mail.MailComposer mailComposer) {
        return new AuthServiceImpl(store, tokenFacade, sessionService, passwordPort, mailService, mailComposer);
    }

    @Bean
    public MfaService mfaService(Store store, TokenFacade tokenFacade, SessionService sessionService) {
        return new MfaServiceImpl(store, tokenFacade, sessionService);
    }
}

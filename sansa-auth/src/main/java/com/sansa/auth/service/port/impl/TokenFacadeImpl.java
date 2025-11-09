package com.sansa.auth.service.port.impl;

import com.sansa.auth.dto.login.LoginTokens;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.util.TokenIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenFacadeImpl implements TokenFacade {

    private final TokenIssuer tokenIssuer;

    @Override
    public LoginTokens issueAfterAuth(String userId, List<String> authorities) {
        // TokenIssuer のシグネチャに合わせる
        // ここでは元実装に従い、最低限の委譲だけ行う
        final String accessToken = tokenIssuer.issueAccessToken(userId, 1);
        final String refreshId   = tokenIssuer.newRefreshId();
        final String refreshToken = tokenIssuer.issueRefreshToken(userId, refreshId);
        return LoginTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public LoginTokens rotate(String refreshToken) {
        // 元の TokenIssuer API に合わせる
        final TokenIssuer.RefreshPayload payload = tokenIssuer.parseRefreshToken(refreshToken);

        // 旧RTのJTI等の検証や無効化は TokenIssuer / Store 側ポリシーに委譲する前提
        final String newRefreshId = tokenIssuer.newRefreshId();
        final String accessToken  = tokenIssuer.issueAccessToken(payload.userId(), 1);
        final String newRefreshToken = tokenIssuer.issueRefreshToken(payload.userId(), newRefreshId);

        return LoginTokens.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}

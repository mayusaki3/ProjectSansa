package com.sansa.auth.service;

import com.sansa.auth.dto.auth.VerifyEmailRequest;
import com.sansa.auth.dto.auth.VerifyEmailResponse;
import com.sansa.auth.exception.InvalidCodeException;
import com.sansa.auth.store.InmemStore;
import com.sansa.auth.util.Idx;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    InmemStore store;

    @Test
    @DisplayName("UT-02-003: verify-email 正常系 -> preRegId と expiresIn を返す")
    void verifyEmail_ok_returns_preRegId() {
        // arrange
        String email = "Valid+tag@Example.COM";
        String code  = "123456";
        String preRegId = "PRE123";
        long ttl = 600L;

        // normEmail を意識せずテストできるよう anyString() でスタブ
        when(store.verifyEmailCode(anyString(), eq(code))).thenReturn(true);
        when(store.createPreReg(anyString(), any())).thenReturn(preRegId);
        when(store.getPreRegTtl(eq(preRegId), any())).thenReturn(ttl);

        VerifyEmailRequest req = VerifyEmailRequest.builder()
                .email(email)
                .code(code)
                .build();

        // act
        VerifyEmailResponse res = authService.verifyEmail(req);

        // assert
        assertThat(res.getPreRegId()).isEqualTo(preRegId);
        assertThat(res.getExpiresIn()).isEqualTo((int) ttl);
    }

    @Test
    @DisplayName("UT-02-004: verify-email 不正/期限切れコード -> InvalidCodeException")
    void verifyEmail_expired_or_invalid() {
        // arrange
        String email = "user@example.com";
        String badCode = "000000";
        // 失敗系はこの1スタブのみ（不要スタブを置かない）
        when(store.verifyEmailCode(eq(Idx.normEmail(email)), eq(badCode))).thenReturn(false);

        VerifyEmailRequest req = VerifyEmailRequest.builder()
                .email(email)
                .code(badCode)
                .build();

        // assert
        assertThrows(InvalidCodeException.class, () -> authService.verifyEmail(req));
    }
}

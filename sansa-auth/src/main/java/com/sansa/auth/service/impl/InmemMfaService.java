package com.sansa.auth.service.impl;

import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.mfa.MfaEmailSendRequest;
import com.sansa.auth.dto.mfa.MfaEmailVerifyRequest;
import com.sansa.auth.dto.mfa.MfaRecoveryIssueResponse;
import com.sansa.auth.dto.mfa.MfaRecoveryVerifyRequest;
import com.sansa.auth.dto.mfa.MfaTotpActivateRequest;
import com.sansa.auth.dto.mfa.MfaTotpEnrollResponse;
import com.sansa.auth.dto.mfa.MfaTotpVerifyRequest;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.UnauthorizedException;
import com.sansa.auth.service.MfaService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * inmem プロファイル用のダミー MFA サービス実装。
 *
 * - 現時点ではすべて UnsupportedOperationException を送出するだけ。
 * - ApplicationContext 初期化のために Bean を 1 つ用意する目的。
 * - 実際の MFA 挙動は別プロファイル（例: cassandra）側で実装する前提。
 */
@Service
@Profile("inmem")
public class InmemMfaService implements MfaService {

    // ---- TOTP ----

    @Override
    public MfaTotpEnrollResponse totpEnroll()
            throws UnauthorizedException, BadRequestException {
        throw new UnsupportedOperationException("MFA is not available in inmem profile.");
    }

    @Override
    public void totpActivate(MfaTotpActivateRequest req)
            throws UnauthorizedException, BadRequestException {
        throw new UnsupportedOperationException("MFA is not available in inmem profile.");
    }

    @Override
    public LoginResponse totpVerify(MfaTotpVerifyRequest req)
            throws UnauthorizedException, BadRequestException {
        throw new UnsupportedOperationException("MFA is not available in inmem profile.");
    }

    // ---- Email OTP ----

    @Override
    public void emailSend(MfaEmailSendRequest req)
            throws UnauthorizedException, BadRequestException {
        throw new UnsupportedOperationException("MFA is not available in inmem profile.");
    }

    @Override
    public LoginResponse emailVerify(MfaEmailVerifyRequest req)
            throws UnauthorizedException, BadRequestException {
        throw new UnsupportedOperationException("MFA is not available in inmem profile.");
    }

    // ---- Recovery Code ----

    @Override
    public MfaRecoveryIssueResponse recoveryIssue()
            throws UnauthorizedException, BadRequestException {
        throw new UnsupportedOperationException("MFA is not available in inmem profile.");
    }

    @Override
    public LoginResponse recoveryVerify(MfaRecoveryVerifyRequest req)
            throws UnauthorizedException, BadRequestException {
        throw new UnsupportedOperationException("MFA is not available in inmem profile.");
    }
}

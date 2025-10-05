package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos.EmailOtpVerifyRequest;
import com.sansa.auth.dto.Dtos.TotpVerifyRequest;
import org.springframework.stereotype.Service;

/**
 * Multi-Factor Authentication service for TOTP and Email OTP verification.
 * This class is called from MfaController.
 */
@Service
public class MfaService {

    /**
     * Verify TOTP code from user input.
     *
     * @param req Request containing the TOTP code
     * @return true if verification succeeds
     */
    public boolean verifyTotp(TotpVerifyRequest req) {
        // TODO: 実際のTOTP検証ロジックをここに実装する
        // 例：GoogleAuthenticator互換のTOTP検証を行う
        String code = req.getCode(); // recordフィールド名に合わせる
        return code != null && code.equals("123456"); // 仮実装
    }

    /**
     * Verify Email OTP code from user input.
     *
     * @param req Request containing the email OTP
     * @return true if verification succeeds
     */
    public boolean verifyEmailOtp(EmailOtpVerifyRequest req) {
        // TODO: 実際のメールOTP検証を実装する
        String code = req.getCode();
        return code != null && code.equals("654321"); // 仮実装
    }
}

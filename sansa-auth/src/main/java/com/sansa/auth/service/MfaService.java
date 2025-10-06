package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos;
import org.springframework.stereotype.Service;

@Service
public class MfaService {

    public Dtos.AuthResult verifyTotp(Dtos.TotpVerifyRequest req) {
        // 実装は後続で詰める
        return Dtos.AuthResult.ok("TOTP verified");
    }

    // 送信用は String email を受ける
    public Dtos.AuthResult sendEmailOtp(String email) {
        // 実装は後続で詰める
        return Dtos.AuthResult.ok("Email OTP sent to " + email);
    }

    public Dtos.AuthResult verifyEmailOtp(Dtos.EmailOtpVerifyRequest req) {
        // 実装は後続で詰める
        return Dtos.AuthResult.ok("Email OTP verified for " + req.getEmail());
    }
}

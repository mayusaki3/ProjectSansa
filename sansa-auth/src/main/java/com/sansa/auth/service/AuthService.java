package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos;

/**
 * Controller からは DTO を渡し、既存実装（String引数のまま）へは
 * デフォルトメソッドでブリッジします。
 */
public interface AuthService {

    // 既存実装が持っている（はずの）シグネチャを残す
    Dtos.AuthResult preRegister(String email);
    Dtos.AuthResult verifyEmail(String email, String code);
    Dtos.AuthResult register(String preRegId, String accountId, String language);

    // --- Controller から DTO を渡せるようにするブリッジ（追加） ---

    default Dtos.AuthResult preRegister(Dtos.PreRegisterRequest req) {
        return preRegister(req.getEmail());
    }

    default Dtos.AuthResult verifyEmail(Dtos.VerifyEmailRequest req) {
        return verifyEmail(req.getEmail(), req.getCode());
    }

    default Dtos.AuthResult register(Dtos.RegisterRequest req) {
        String pre = (req.getPreRegId() != null) ? req.getPreRegId().toString() : null;
        String acc = (req.getAccountId() != null) ? req.getAccountId().toString() : null;
        return register(pre, acc, req.getLanguage());
    }
}

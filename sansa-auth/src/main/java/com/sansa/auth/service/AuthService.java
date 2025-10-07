package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.dto.Dtos.AuthResult;
import com.sansa.auth.model.Models;

public interface AuthService {
    // 実装クラスがオーバーライドする“本体”
    Dtos.AuthResult preRegister(String email, String language);
    Dtos.AuthResult verifyEmail(String preRegId, String code);
    Dtos.AuthResult register(String preRegId, String accountId, String language);

    // DTO からの変換だけを担当（実処理なし）
    default AuthResult preRegister(Dtos.PreRegisterRequest req) {
        return preRegister(req.getEmail(), req.getLanguage());
    }
    default AuthResult verifyEmail(Dtos.VerifyEmailRequest req) {
        return verifyEmail(req.getPreRegId(), req.getCode());
    }
    default AuthResult register(Dtos.RegisterRequest req) {
        return register(req.getPreRegId(), req.getAccountId(), req.getLanguage());
    }
}

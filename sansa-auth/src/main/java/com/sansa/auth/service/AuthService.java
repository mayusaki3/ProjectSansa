package com.sansa.auth.service;

import com.sansa.auth.dto.auth.PreRegisterRequest;
import com.sansa.auth.dto.auth.PreRegisterResponse;
import com.sansa.auth.dto.auth.RegisterRequest;
import com.sansa.auth.dto.auth.RegisterResponse;
import com.sansa.auth.dto.auth.VerifyEmailRequest;
import com.sansa.auth.dto.auth.VerifyEmailResponse;

/**
 * 認証サービス（登録フロー）
 * API仕様および UT仕様 M01_UT02（登録フロー）に準拠
 */
public interface AuthService {

    /**
     * 01-01 pre-register（事前登録）
     */
    PreRegisterResponse preRegister(PreRegisterRequest request);

    /**
     * 01-02 verify-email（メール認証コード検証 / preRegId発行）
     */
    VerifyEmailResponse verifyEmail(VerifyEmailRequest request);

    /**
     * 01-03 register（本登録）
     */
    RegisterResponse register(RegisterRequest request);
}

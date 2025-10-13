package com.sansa.auth.service;

import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.webauthn.*;
import com.sansa.auth.exception.BadRequestException;
import com.sansa.auth.exception.NotFoundException;
import com.sansa.auth.exception.UnauthorizedException;

/**
 * WebAuthn（パスキー）ユースケース（/webauthn 配下）。
 * 参照仕様: 03_WebAuthn.md（register/options, register/verify, challenge, assertion, credentials） :contentReference[oaicite:13]{index=13}
 */
public interface WebAuthnService {

    /** 登録オプション取得。GET /webauthn/register/options
     *  仕様: 03_WebAuthn.md 「#1 → WebAuthnRegisterOptionsResponse」 :contentReference[oaicite:14]{index=14}
     */
    WebAuthnRegisterOptionsResponse registerOptions()
            throws UnauthorizedException, BadRequestException;

    /** 登録検証（Attestation）。POST /webauthn/register/verify
     *  仕様: 03_WebAuthn.md 「#2 → WebAuthnRegisterVerifyResponse」 :contentReference[oaicite:15]{index=15}
     */
    WebAuthnRegisterVerifyResponse registerVerify(WebAuthnRegisterVerifyRequest req)
            throws UnauthorizedException, BadRequestException;

    /** 認証チャレンジ取得。GET /webauthn/challenge
     *  仕様: 03_WebAuthn.md 「#3 → WebAuthnChallengeResponse」 :contentReference[oaicite:16]{index=16}
     */
    WebAuthnChallengeResponse challenge()
            throws UnauthorizedException, BadRequestException;

    /** 認証アサーション検証。POST /webauthn/assertion
     *  仕様: 03_WebAuthn.md 「#4 → LoginResponse」 :contentReference[oaicite:17]{index=17}
     */
    LoginResponse assertion(WebAuthnAssertionRequest req)
            throws UnauthorizedException, BadRequestException;

    /** 登録済みクレデンシャル一覧。GET /webauthn/credentials
     *  仕様: 03_WebAuthn.md 「#5 → WebAuthnCredentialListResponse」 :contentReference[oaicite:18]{index=18}
     */
    WebAuthnCredentialListResponse listCredentials()
            throws UnauthorizedException;

    /** クレデンシャル失効。DELETE /webauthn/credentials/{credentialId}
     *  仕様: 03_WebAuthn.md 「#6」204/404 :contentReference[oaicite:19]{index=19}
     *  成功: 204（本文なし）
     */
    void deleteCredential(String credentialId)
            throws UnauthorizedException, NotFoundException;
}

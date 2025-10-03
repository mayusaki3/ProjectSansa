package com.sansa.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Dtos {

    public record PreRegisterRequest(@NotBlank @Email String email) {}

    public record VerifyEmailRequest(@NotBlank @Email String email,
                                     @NotBlank @Size(min=6, max=6) String code) {}

    public record RegisterRequest(@NotBlank String preRegId,
                                  @NotBlank @Size(min=3, max=32) String accountId,
                                  String language) {}

    public record WebAuthnChallengeResponse(String challenge, String rpId, String userVerification, long timeoutMs) {}

    public record WebAuthnAssertionRequest(@NotBlank String id,
                                           @NotBlank String clientDataJSON,
                                           @NotBlank String authenticatorData,
                                           @NotBlank String signature,
                                           String userHandle) {}

    public record LoginResponse(String accessToken, String refreshToken, long expiresInSec) {}

    public record MfaRequiredResponse(String error, String challengeId, String[] factors, long expiresInSec) {}

    public record TotpVerifyRequest(@NotBlank String challengeId, @NotBlank @Size(min=6, max=6) String code,
                                    String deviceId, Boolean remember) {}

    public record EmailOtpRequest(@NotBlank String challengeId) {}

    public record EmailOtpVerifyRequest(@NotBlank String challengeId, @NotBlank @Size(min=6, max=6) String code,
                                        String deviceId, Boolean remember) {}

    public record LogoutAllResponse(boolean ok, long tokenVersion) {}
}

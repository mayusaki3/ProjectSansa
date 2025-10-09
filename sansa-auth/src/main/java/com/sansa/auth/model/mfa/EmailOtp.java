package com.sansa.auth.model.mfa;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Value @Builder(toBuilder = true)
public class EmailOtp {
  UUID userId;
  String challengeId;
  String code;
  Instant expiresAt;
  boolean consumed;
  Instant issuedAt;
}

package com.sansa.auth.model.prereg;

import lombok.*;
import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class EmailVerificationCode {
  String emailNormalized;
  String code;          // latest only valid
  Instant issuedAt;
  Instant expiresAt;
}

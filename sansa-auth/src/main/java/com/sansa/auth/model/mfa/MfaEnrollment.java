package com.sansa.auth.model.mfa;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Value @Builder(toBuilder = true)
public class MfaEnrollment {
  UUID userId;
  boolean totpEnabled;
  boolean emailEnabled;
  boolean recoveryEnabled;
  List<String> factors;  // ["totp","email_otp",...]
}

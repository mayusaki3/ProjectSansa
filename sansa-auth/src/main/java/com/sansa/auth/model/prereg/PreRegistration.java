package com.sansa.auth.model.prereg;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class PreRegistration {
  @Builder.Default UUID id = UUID.randomUUID(); // preRegId
  String emailNormalized;
  Instant expiresAt;
  boolean consumed;
}

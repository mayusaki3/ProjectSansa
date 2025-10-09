package com.sansa.auth.model.session;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class Session {
  @Builder.Default UUID sessionId = UUID.randomUUID();
  UUID userId;
  Instant issuedAt;
  Instant lastActive;
  Instant expiresAt;
  List<String> amr;
}

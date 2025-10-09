package com.sansa.auth.model.session;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class RefreshTokenRecord {
  UUID tokenId;
  UUID userId;
  int tokenVersion;
  Instant expiresAt;
  boolean revoked;
  Instant revokedAt;
  String familyId;  // 任意: RTファミリをハンドリングするID
}

package com.sansa.auth.model.user;

import lombok.*;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class User {
  @Builder.Default UUID userId = UUID.randomUUID();
  String emailNormalized;     // lower-cased
  String accountId;
  String displayName;
  boolean emailVerified;
  // 他、作成/更新日時は別途
}

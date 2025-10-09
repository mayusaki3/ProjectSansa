package com.sansa.auth.model.mfa;

import lombok.*;
import java.util.UUID;

@Value @Builder(toBuilder = true)
public class RecoveryCode {
  UUID userId;
  String codeHash;   // 平文ではなくハッシュ保存を推奨
  boolean consumed;
}

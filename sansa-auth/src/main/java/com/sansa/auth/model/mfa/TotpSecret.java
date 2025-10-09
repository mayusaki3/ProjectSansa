package com.sansa.auth.model.mfa;

import lombok.*;
import java.util.UUID;

@Value @Builder(toBuilder = true)
public class TotpSecret {
  UUID userId;
  String secret;     // server-side保存
  boolean activated; // activate 済か
}

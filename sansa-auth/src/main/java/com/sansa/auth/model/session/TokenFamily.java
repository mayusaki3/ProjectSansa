package com.sansa.auth.model.session;

import lombok.*;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class TokenFamily {
  UUID userId;
  int tokenVersion;  // logout_all で ++
}

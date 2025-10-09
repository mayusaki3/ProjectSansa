package com.sansa.auth.dto.auth;

import lombok.*;

@Value
@Builder
public class RegisterResponse {
  boolean success;
  String userId;        // String(UUID)
  boolean emailVerified;
}

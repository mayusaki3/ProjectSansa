package com.sansa.auth.dto.sessions;

import lombok.*;

@Value @Builder
public class LogoutResponse {
  boolean success;
}

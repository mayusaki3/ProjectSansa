package com.sansa.auth.dto.auth;

import lombok.*;

@Value
@Builder
public class PreRegisterResponse {
  boolean success;
  Long throttleMs;
}

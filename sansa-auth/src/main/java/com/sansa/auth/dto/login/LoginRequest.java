package com.sansa.auth.dto.login;

import jakarta.validation.constraints.*;
import lombok.*;

@Value
@Builder
public class LoginRequest {
  @NotBlank String identifier; // email or accountId
  @NotBlank String password;
}

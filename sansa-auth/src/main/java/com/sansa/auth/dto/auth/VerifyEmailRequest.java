package com.sansa.auth.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Value
@Builder
public class VerifyEmailRequest {
  @NotBlank @Email String email;
  @NotBlank @Size(min = 6, max = 10) String code;
}

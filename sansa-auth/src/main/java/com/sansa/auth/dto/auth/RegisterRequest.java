package com.sansa.auth.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Value
@Builder
public class RegisterRequest {
  @NotBlank String preRegId;               // UUID String
  @NotBlank @Size(min=3, max=64) String accountId;
  @Size(min=8, max=128) String password;   // 任意（パスキー主体ならnull許容）
  @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$")
  String language;
}

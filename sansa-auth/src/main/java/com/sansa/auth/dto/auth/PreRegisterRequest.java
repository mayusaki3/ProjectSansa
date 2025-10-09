package com.sansa.auth.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Value
@Builder
public class PreRegisterRequest {
  @NotBlank @Email String email;
  // RFC5646: ja / ja-JP / en-US
  @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$")
  String language;
}

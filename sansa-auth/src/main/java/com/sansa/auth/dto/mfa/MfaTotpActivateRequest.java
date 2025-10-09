package com.sansa.auth.dto.mfa;

import lombok.*;
import jakarta.validation.constraints.*;

@Value @Builder
public class MfaTotpActivateRequest {
  @NotBlank @Size(min=6, max=10) String code;
}

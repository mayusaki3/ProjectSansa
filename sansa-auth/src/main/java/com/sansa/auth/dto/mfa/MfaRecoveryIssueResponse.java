package com.sansa.auth.dto.mfa;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Value @Builder
public class MfaRecoveryIssueResponse {
  List<String> recoveryCodes;
}

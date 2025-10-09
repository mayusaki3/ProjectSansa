package com.sansa.auth.dto.common;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Value
@Builder
public class ProblemDetail {
  @NotBlank String type;
  @NotBlank String title;
  @NotNull Integer status;
  String detail;
  String instance;
  List<FieldError> errors;
  String traceId;

  @Value @Builder
  public static class FieldError {
    @NotBlank String field;
    @NotBlank String reason;
  }
}

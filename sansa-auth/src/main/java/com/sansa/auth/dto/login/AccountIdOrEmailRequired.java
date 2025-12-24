package com.sansa.auth.dto.login;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * accountId または email のどちらか一方以上が必須であることを検証する制約。
 */
@Documented
@Constraint(validatedBy = AccountIdOrEmailRequiredValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountIdOrEmailRequired {
    String message() default "accountId or email is required";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

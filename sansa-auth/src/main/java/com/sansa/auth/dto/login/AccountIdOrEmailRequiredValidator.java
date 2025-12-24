package com.sansa.auth.dto.login;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link AccountIdOrEmailRequired} の検証ロジック。
 *
 * 仕様優先:
 * - accountId/email の「どちらか必須」の違反は Controller の errors[].field を "identifier" として出力する
 *   （ドキュメント/テスト仕様: AUTH-CTRL-UT-005 に合わせる）
 */
public class AccountIdOrEmailRequiredValidator implements ConstraintValidator<AccountIdOrEmailRequired, LoginRequest> {

    @Override
    public boolean isValid(LoginRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null は別途 @NotNull 等で扱う
        }

        boolean hasAccountId = value.getAccountId() != null && !value.getAccountId().trim().isEmpty();
        boolean hasEmail = value.getEmail() != null && !value.getEmail().trim().isEmpty();

        if (hasAccountId || hasEmail) {
            return true;
        }

        // field を "identifier" として固定化して errors[].field を安定させる
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("accountId or email is required")
                .addPropertyNode("identifier")
                .addConstraintViolation();
        return false;
    }
}

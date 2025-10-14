package com.sansa.auth.controller;

import com.sansa.auth.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 全コントローラー共通の例外→ProblemDetail応答
 *
 * 仕様:
 *  - Content-Type: application/problem+json
 *  - Content-Language: リクエストのLocaleを反映
 *  - 400 invalid-argument（バリデーション）
 *  - 401 invalid-credentials
 *  - 404 session-not-found などリソース系
 *  - 429 rate-limit
 *  - 500 internal-error（フォールバック）
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    // ===== 400: リクエストバリデーション失敗（Bean Validation）=====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleInvalidArgs(MethodArgumentNotValidException ex, Locale locale) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toError)
                .collect(Collectors.toList());

        Map<String, Object> extra = new LinkedHashMap<>();
        extra.put("errors", errors);

        return problem(
                HttpStatus.BAD_REQUEST,
                "invalid-argument",
                "validation failed",
                "invalid-argument",
                locale,
                extra
        );
    }

    // PathVariable等の@Validated違反
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, Locale locale) {
        List<Map<String, String>> errors = ex.getConstraintViolations().stream()
                .map(v -> Map.of("field", v.getPropertyPath().toString(), "message", v.getMessage()))
                .collect(Collectors.toList());

        Map<String, Object> extra = new LinkedHashMap<>();
        extra.put("errors", errors);

        return problem(
                HttpStatus.BAD_REQUEST,
                "invalid-argument",
                "validation failed",
                "invalid-argument",
                locale,
                extra
        );
    }

    // ===== 401: 認証失敗 =====
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(InvalidCredentialsException ex, Locale locale) {
        return problem(HttpStatus.UNAUTHORIZED, "invalid-credentials", ex.getMessage(), "invalid-credentials", locale, null);
    }

    // ===== 404: セッション等の未検出 =====
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleSessionNotFound(SessionNotFoundException ex, Locale locale) {
        return problem(HttpStatus.NOT_FOUND, "session-not-found", ex.getMessage(), "session-not-found", locale, null);
    }

    // ===== 404: その他汎用NotFound =====
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NotFoundException ex, Locale locale) {
        return problem(HttpStatus.NOT_FOUND, "not-found", ex.getMessage(), "not-found", locale, null);
    }

    // ===== 401/400系の汎用 =====
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorized(UnauthorizedException ex, Locale locale) {
        return problem(HttpStatus.UNAUTHORIZED, "unauthorized", ex.getMessage(), "unauthorized", locale, null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(BadRequestException ex, Locale locale) {
        return problem(HttpStatus.BAD_REQUEST, "invalid-argument", ex.getMessage(), "invalid-argument", locale, null);
    }

    @ExceptionHandler(GoneException.class)
    public ResponseEntity<ProblemDetail> handleGone(GoneException ex, Locale locale) {
        return problem(HttpStatus.GONE, "gone", ex.getMessage(), "gone", locale, null);
    }

    // ===== 429: レート制限 =====
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ProblemDetail> handleRateLimit(RateLimitException ex, Locale locale) {
        return problem(HttpStatus.TOO_MANY_REQUESTS, "rate-limit", ex.getMessage(), "rate-limit", locale, null);
    }

    // ===== フォールバック（予期しない例外）=====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAny(Exception ex, Locale locale, HttpServletRequest req) {
        // 必要ならログ
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "unexpected error", "internal-error", locale, null);
    }

    // ====== 共通ユーティリティ ======
    private Map<String, String> toError(FieldError fe) {
        return Map.of(
                "field", fe.getField(),
                "message", Optional.ofNullable(fe.getDefaultMessage()).orElse("invalid")
        );
    }

    private ResponseEntity<ProblemDetail> problem(
            HttpStatus status,
            String title,
            String detail,
            String code,
            Locale locale,
            Map<String, Object> extra // null可: {"errors":[...]}等
    ) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        if (title != null) {
            pd.setTitle(title);
        }
        if (detail != null) {
            pd.setDetail(detail);
        }
        if (code != null) {
            pd.setProperty("code", code);
        }
        if (extra != null) {
            extra.forEach(pd::setProperty);
        }
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .header(HttpHeaders.CONTENT_LANGUAGE, locale != null ? locale.toLanguageTag() : Locale.getDefault().toLanguageTag())
                .body(pd);
    }
}

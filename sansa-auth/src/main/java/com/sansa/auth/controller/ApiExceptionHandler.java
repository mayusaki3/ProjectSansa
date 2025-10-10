package com.sansa.auth.controller;

import com.sansa.auth.exception.InvalidCredentialsException;
import com.sansa.auth.exception.RateLimitException;
import com.sansa.auth.exception.SessionNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.*;

/**
 * API 共通の例外ハンドラ
 * - application/problem+json
 * - Content-Language を常に付与
 * - type=urn:problem:<code>
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.sansa.auth")
public class ApiExceptionHandler {

    // 共通の ProblemDetail レスポンス生成
    private static ResponseEntity<ProblemDetail> problem(
            HttpStatus status, String title, String detail, String code, Locale locale, Map<String, Object> extras) {

        ProblemDetail pd = ProblemDetail.forStatus(status);
        if (title != null) pd.setTitle(title);
        if (detail != null) pd.setDetail(detail);
        if (code != null) {
            pd.setType(URI.create("urn:problem:" + code));
            pd.setProperty("code", code);
        }
        if (extras != null) {
            extras.forEach(pd::setProperty);
        }
        String lang = (locale != null) ? locale.toLanguageTag() : Locale.getDefault().toLanguageTag();

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .header(HttpHeaders.CONTENT_LANGUAGE, lang)
                .body(pd);
    }

    // ServletException 経由で包まれて来るケースを解凍して振り分ける
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ProblemDetail> handleServlet(ServletException ex, Locale locale) {
        Throwable root = ex.getCause();
        if (root instanceof SessionNotFoundException snf) {
            return handleSessionNotFound(snf, locale);
        }
        if (root instanceof InvalidCredentialsException ice) {
            return handleInvalidCredentials(ice, locale); // 既存の 401 ハンドラを呼ぶ（下にあるはず）
        }
        if (root instanceof RateLimitException rle) {
            return handleRateLimit(rle, locale); // 既存の 429 ハンドラ
        }
        // それ以外はフォールバック 500
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "unexpected error", "internal-error", locale, null);
    }

    // ===== 400: @Valid のボディ検証失敗（field errors を errors[] に格納） =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
        List<Map<String, Object>> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("field", fe.getField());
            m.put("message", fe.getDefaultMessage());
            errors.add(m);
        });
        Map<String, Object> extras = Map.of("errors", errors);
        return problem(HttpStatus.BAD_REQUEST, "invalid-argument", "validation failed", "invalid-argument", locale, extras);
    }

    // ===== 400: クエリ/パス等の制約違反 =====
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, Locale locale) {
        List<Map<String, Object>> errors = new ArrayList<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            Map<String, Object> m = new LinkedHashMap<>();
            // propertyPath: e.g. "preRegister.req.email"
            m.put("field", v.getPropertyPath() != null ? v.getPropertyPath().toString() : null);
            m.put("message", v.getMessage());
            errors.add(m);
        }
        Map<String, Object> extras = Map.of("errors", errors);
        return problem(HttpStatus.BAD_REQUEST, "invalid-argument", "validation failed", "invalid-argument", locale, extras);
    }

    // ===== 400: その他のバインドエラー（@ModelAttribute 等） =====
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException ex, Locale locale) {
        List<Map<String, Object>> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("field", fe.getField());
            m.put("message", fe.getDefaultMessage());
            errors.add(m);
        });
        Map<String, Object> extras = Map.of("errors", errors);
        return problem(HttpStatus.BAD_REQUEST, "invalid-argument", "validation failed", "invalid-argument", locale, extras);
    }

    // ===== 401: ログイン失敗 =====
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(InvalidCredentialsException ex, Locale locale) {
        return problem(HttpStatus.UNAUTHORIZED, "invalid-credentials", ex.getMessage(), "invalid-credentials", locale, null);
    }

    // ===== 404: セッション未検出 =====
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleSessionNotFound(SessionNotFoundException ex, Locale locale) {
        return problem(HttpStatus.NOT_FOUND, "session-not-found", ex.getMessage(), "session-not-found", locale, null);
    }

    // ===== 429: レート制限 =====
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ProblemDetail> handleRateLimit(RateLimitException ex, Locale locale) {
        return problem(HttpStatus.TOO_MANY_REQUESTS, "rate-limit", ex.getMessage(), "rate-limit", locale, null);
    }

    // ===== フォールバック（予期しない例外） =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAny(Exception ex, Locale locale) {
        // ここはログ出力などお好みで
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "unexpected error", "internal-error", locale, null);
    }
}

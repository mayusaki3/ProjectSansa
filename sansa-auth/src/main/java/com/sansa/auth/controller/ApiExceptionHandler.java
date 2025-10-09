package com.sansa.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.time.Duration;
import java.util.*;

/**
 * application/problem+json を返す ControllerAdvice
 * 400: invalid-argument (+ errors[])
 * 401: auth/invalid-credentials
 * 404: not-found（呼び出し側で type 指定可）
 * 429: rate-limit（Retry-After 付与）
 */
@ControllerAdvice
public class ApiExceptionHandler {

    // 401: 認証失敗（Securityへ依存しない独自例外）
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String msg) { super(msg); }
    }

    // 404: 未検出（type を上書き可能）
    public static class NotFoundException extends RuntimeException {
        private final String type;
        public NotFoundException(String type, String msg) { super(msg); this.type = type; }
        public String getType() { return type; }
    }

    // 429: レート制限
    public static class RateLimitException extends RuntimeException {
        private final Duration retryAfter;
        public RateLimitException(Duration retryAfter, String msg) { super(msg); this.retryAfter = retryAfter; }
        public Duration getRetryAfter() { return retryAfter; }
    }

    // ---- 400: @Valid でのバリデーション失敗 ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            Locale locale
    ) {
        ProblemDetail body = base(HttpStatus.BAD_REQUEST,
                "Invalid argument",
                "invalid-argument",
                null,
                request.getRequestURI());

        // errors[] = [{field, code, message}]
        List<Map<String, Object>> errors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("field", fe.getField());
            item.put("code", fe.getCode());
            item.put("message", fe.getDefaultMessage());
            errors.add(item);
        }
        if (!errors.isEmpty()) body.setProperty("errors", errors);

        return withHeaders(body, locale);
    }

    // ---- 400: ConstraintViolation 等 ----
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request,
            Locale locale
    ) {
        ProblemDetail body = base(HttpStatus.BAD_REQUEST,
                "Invalid argument",
                "invalid-argument",
                ex.getMessage(),
                request.getRequestURI());
        return withHeaders(body, locale);
    }

    // ---- 401: 認証失敗 ----
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request,
            Locale locale
    ) {
        ProblemDetail body = base(HttpStatus.UNAUTHORIZED,
                "Invalid credentials",
                "auth/invalid-credentials",
                ex.getMessage(),
                request.getRequestURI());
        return withHeaders(body, locale);
    }

    // ---- 404: 未検出 ----
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request,
            Locale locale
    ) {
        String type = (ex.getType() != null ? ex.getType() : "not-found");
        ProblemDetail body = base(HttpStatus.NOT_FOUND,
                "Not found",
                type,
                ex.getMessage(),
                request.getRequestURI());
        return withHeaders(body, locale);
    }

    // ---- 429: レート制限 ----
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ProblemDetail> handleRateLimit(
            RateLimitException ex,
            HttpServletRequest request,
            Locale locale
    ) {
        ProblemDetail body = base(HttpStatus.TOO_MANY_REQUESTS,
                "Too Many Requests",
                "rate-limit",
                ex.getMessage(),
                request.getRequestURI());

        HttpHeaders headers = defaultHeaders(locale);
        if (ex.getRetryAfter() != null && !ex.getRetryAfter().isNegative()) {
            headers.set("Retry-After", String.valueOf(ex.getRetryAfter().toSeconds()));
        }
        return new ResponseEntity<>(body, headers, HttpStatus.TOO_MANY_REQUESTS);
    }

    // ========= helper =========

    private static ProblemDetail base(HttpStatus status, String title, String type, String detail, String instancePath) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(type));             // $.type
        pd.setInstance(URI.create(instancePath)); // $.instance
        pd.setProperty("status", status.value()); // $.status を明示で持たせる（テスト整合用）
        return pd;
    }

    private static ResponseEntity<ProblemDetail> withHeaders(ProblemDetail body, Locale locale) {
        return new ResponseEntity<>(body, defaultHeaders(locale), HttpStatus.valueOf(body.getStatus()));
    }

    private static HttpHeaders defaultHeaders(Locale locale) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/problem+json");
        headers.set(HttpHeaders.CONTENT_LANGUAGE, toLangTag(locale));
        return headers;
    }

    private static String toLangTag(Locale locale) {
        if (locale == null) return "en";
        if ("ja".equalsIgnoreCase(locale.getLanguage())) return "ja"; // テスト期待: startsWith("ja")
        return locale.toLanguageTag();
    }
}

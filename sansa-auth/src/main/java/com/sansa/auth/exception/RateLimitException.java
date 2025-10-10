// src/main/java/com/sansa/auth/exception/RateLimitException.java
package com.sansa.auth.exception;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) { super(message); }
}

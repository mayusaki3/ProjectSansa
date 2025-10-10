// src/main/java/com/sansa/auth/exception/SessionNotFoundException.java
package com.sansa.auth.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String message) { super(message); }
    public SessionNotFoundException(String message, Throwable cause) { super(message, cause); }
}

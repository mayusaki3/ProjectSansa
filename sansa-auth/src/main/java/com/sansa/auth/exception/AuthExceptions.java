package com.sansa.auth.exception;

public final class AuthExceptions {
  public static class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String type) { super(type); }
  }
  public static class BadRequestException extends RuntimeException {
    public BadRequestException(String type) { super(type); }
  }
  public static class NotFoundException extends RuntimeException {
    public NotFoundException(String type) { super(type); }
  }
  public static class GoneException extends RuntimeException {
    public GoneException(String type) { super(type); }
  }
  private AuthExceptions() {}
}

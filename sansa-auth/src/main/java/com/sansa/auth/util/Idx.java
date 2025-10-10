package com.sansa.auth.util;

public final class Idx {
  public static String normEmail(String email) {
    return email == null ? null : email.trim().toLowerCase();
  }
  public static String normEmailOrNull(String id) {
    if (id == null) return null;
    String s = id.trim();
    return s.contains("@") ? s.toLowerCase() : null;
  }
  private Idx() {}
}

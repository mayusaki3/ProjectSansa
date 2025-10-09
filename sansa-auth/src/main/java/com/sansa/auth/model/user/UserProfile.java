package com.sansa.auth.model.user;

import lombok.*;

@Value @Builder(toBuilder = true)
public class UserProfile {
  String displayName;
  String locale; // ja-JP 等
}

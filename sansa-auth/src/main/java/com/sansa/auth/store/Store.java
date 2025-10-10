package com.sansa.auth.store;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface Store {
  // === Email verify ===
  String issueEmailCode(String normalizedEmail, int ttlSeconds); // 戻り値: code
  boolean verifyEmailCode(String normalizedEmail, String code);  // 見て消す(成功時)
  String createPreReg(String normalizedEmail, Instant expiresAt); // 戻り値: preRegId
  long getPreRegTtl(String preRegId, Instant now);                // 秒

  // === Sessions ===
  List<SessionRecord> listSessions(String accountId);
  boolean revokeSessionById(String sessionId); // true:何か消した
  int revokeAllSessions(String accountId);

  // 必要に応じてレート制限系など…
  boolean tryConsumeRateLimit(String key, int windowSec, int maxCount);

  // 小さなDTO
  record SessionRecord(String id, String device, Instant createdAt, Instant lastSeen) {}
}

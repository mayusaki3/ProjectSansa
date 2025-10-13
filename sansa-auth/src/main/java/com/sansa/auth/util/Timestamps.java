package com.sansa.auth.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 時刻/期限を DTO 非依存で扱うユーティリティ。
 *
 * <p>方針:
 * <ul>
 *   <li>どの DTO にも依存しない（ビルダー有無の揺れに巻き込まれない）。</li>
 *   <li>フロント/テストが扱いやすい「秒」「ミリ秒」「RFC3339」を提供。</li>
 * </ul>
 */
public final class Timestamps {

    private Timestamps() {}

    /** now() を返します。テスト差し替えをしやすくするために 1 箇所に集約。 */
    public static Instant now() {
        return Instant.now();
    }

    /** Instant → epoch seconds（null セーフ; null の場合 0） */
    public static long toEpochSeconds(Instant i) {
        return (i == null) ? 0L : i.getEpochSecond();
    }

    /** Instant → epoch millis（null セーフ; null の場合 0） */
    public static long toEpochMillis(Instant i) {
        return (i == null) ? 0L : i.toEpochMilli();
    }

    /** Instant → RFC3339（UTC固定, 例: 2025-01-23T12:34:56Z。null の場合 ""） */
    public static String toRfc3339Utc(Instant i) {
        if (i == null) return "";
        return OffsetDateTime.ofInstant(i, ZoneOffset.UTC).toString();
    }

    /** now からの残存秒数（負なら 0）。 */
    public static long remainingSeconds(Instant expiresAt, Instant now) {
        if (expiresAt == null || now == null) return 0L;
        long diff = expiresAt.getEpochSecond() - now.getEpochSecond();
        return Math.max(0L, diff);
    }
}

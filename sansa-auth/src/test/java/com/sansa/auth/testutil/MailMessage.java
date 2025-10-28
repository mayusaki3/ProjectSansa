package com.sansa.auth.testutil;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Value;

/**
 * 単純なメールメッセージDTO（テスト用）
 * - 件名/本文/宛先/言語/送信時刻のみ保持
 */
@Value
@Builder
public class MailMessage {
    String subject;
    String body;
    List<String> to;
    String locale;
    Instant createdAt;
}

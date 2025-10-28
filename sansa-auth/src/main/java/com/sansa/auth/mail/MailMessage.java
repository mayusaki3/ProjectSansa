package com.sansa.auth.mail;

import lombok.Builder;

/**
 * メールの最小DTO。
 * - 今はテキスト本文のみ（HTMLが必要ならフィールドを追加）
 * - 多言語の文面は呼び出し側で MessageSource からレンダリングして詰めます
 */
@Builder
public record MailMessage(
        String to,        // 宛先（必須）
        String subject,   // 件名（必須）
        String body       // 本文（必須：プレーンテキスト）
) {
    public MailMessage {
        if (to == null || to.isBlank())      throw new IllegalArgumentException("to is required");
        if (subject == null || subject.isBlank()) throw new IllegalArgumentException("subject is required");
        if (body == null || body.isBlank())  throw new IllegalArgumentException("body is required");
    }
}

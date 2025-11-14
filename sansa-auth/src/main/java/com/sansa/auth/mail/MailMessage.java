package com.sansa.auth.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.Locale;

/**
 * メール送信メッセージ DTO
 * 役割: SmtpMailService 等の共通入力
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailMessage {
    /** From アドレス（単一） */
    private String from;

    /** To / Cc / Bcc は複数想定 */
    @Singular("to")  private List<String> to;
    @Singular("cc")  private List<String> cc;
    @Singular("bcc") private List<String> bcc;

    /** 件名・本文 */
    private String subject;
    private String body;

    /** ロケール（テンプレ選択等で使用する場合） */
    @Builder.Default
    private Locale locale = Locale.JAPAN;
}

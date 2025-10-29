package com.sansa.auth.mail;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Locale;

/**
 * メール送信内容を保持するDTO。
 * - Lombokの@Singularにより、Builderで to("a@b") のような単数追加が可能。
 * - アクセサは標準ゲッター（getTo/getCc/getBcc/...）に統一。
 */
@Getter
@Builder
public class MailMessage {

    /** 送信元（省略時は構成のデフォルトFromを使用） */
    private final String from;

    /** 宛先（複数） */
    @Singular("to")
    private final List<String> to;

    /** CC（複数） */
    @Singular("cc")
    private final List<String> cc;

    /** BCC（複数） */
    @Singular("bcc")
    private final List<String> bcc;

    /** 件名 */
    private final String subject;

    /** 本文（プレーンテキスト前提） */
    private final String body;

    /** ローカライズに使う言語（必要なら） */
    private final Locale locale;
}

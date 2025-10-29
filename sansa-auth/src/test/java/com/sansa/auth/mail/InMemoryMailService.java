package com.sansa.auth.mail;

/**
 * 【TEST-SCOPE】互換クラス。
 * 以前のテストが com.sansa.auth.mail.InMemoryMailService を import している想定のため、
 * InmemOutboxMailSender の別名として提供。
 */
public class InMemoryMailService extends InmemOutboxMailSender {
    // 追加実装は不要。InmemOutboxMailSender の機能をそのまま利用。
}

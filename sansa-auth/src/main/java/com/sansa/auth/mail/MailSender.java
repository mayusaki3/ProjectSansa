package com.sansa.auth.mail;

/**
 * アプリ側が依存する最小限のメール送信インタフェース。
 * 目的：
 *  - 実運用（SMTP/JavaMail など）とテスト（InMemory Outbox）を差し替え可能にする。
 *  - ドメイン層は MailSender のみに依存し、具体実装へは依存しない。
 *
 * 実装例：
 *  - InmemOutboxMailSender（UT/IT inmem 用・配送しない）
 *  - SmtpMailSender（本番や Cassandra-IT の MailHog/SMTP 用）
 */
public interface MailSender {

  /**
   * メールを送信（または Outbox に蓄積）する。
   * 失敗時の例外送出方針は実装依存（運用実装は例外、inmem は基本成功）。
   *
   * @param message 送信するメールの内容
   */
  void send(MailMessage message);
}

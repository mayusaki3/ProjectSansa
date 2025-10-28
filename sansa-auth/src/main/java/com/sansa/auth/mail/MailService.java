package com.sansa.auth.mail;

/**
 * メール送信の抽象インタフェース。
 * - 本番/IT（cassandraプロファイル）では SMTP 実装（SmtpMailService）がバインドされます。
 * - UT/IT（inmemプロファイル）では InMemory 実装（InMemoryMailService）がバインドされます。
 *
 * 呼び出し側はプロファイル差を意識せず、統一して MailService#send を呼び出します。
 */
public interface MailService {

    /**
     * メールを送信（またはOutBoxへ蓄積）します。
     * @param msg 送信内容（宛先/件名/本文 は必須）
     * @throws IllegalArgumentException 必須項目欠落時
     */
    void send(MailMessage msg);
}

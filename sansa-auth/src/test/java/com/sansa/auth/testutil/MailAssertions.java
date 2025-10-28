package com.sansa.auth.testutil;

import org.junit.jupiter.api.Assertions;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** メール本文の簡易検証ユーティリティ */
public final class MailAssertions {
    private MailAssertions() {}

    /** 本文から6桁コードを抽出（見つからなければfail） */
    public static String extract6DigitCode(String body) {
        Pattern p = Pattern.compile("\\b(\\d{6})\\b");
        Matcher m = p.matcher(body);
        if (!m.find()) {
            Assertions.fail("6-digit code not found in mail body. body=" + body);
        }
        return m.group(1);
    }

    public static void assertJaSubject(String actual, String expectedPrefix) {
        Assertions.assertTrue(actual.startsWith(expectedPrefix),
                () -> "Japanese subject mismatch. actual=" + actual + ", expectedPrefix=" + expectedPrefix);
    }

    public static void assertEnSubject(String actual, String expectedPrefix) {
        Assertions.assertTrue(actual.toLowerCase(Locale.ROOT).startsWith(expectedPrefix.toLowerCase(Locale.ROOT)),
                () -> "English subject mismatch. actual=" + actual + ", expectedPrefix=" + expectedPrefix);
    }
}

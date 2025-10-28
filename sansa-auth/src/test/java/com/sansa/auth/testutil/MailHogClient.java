package com.sansa.auth.testutil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MailHog HTTP API v2 用の軽量クライアント（テスト専用）。
 * 既定URL: http://localhost:8025
 * 上書き:
 *   -Dmailhog.baseUrl=...
 *   環境変数 MAILHOG_BASE_URL=...
 */
public class MailHogClient {

    private static final String SYS_PROP = "mailhog.baseUrl";
    private static final String ENV = "MAILHOG_BASE_URL";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper om = new ObjectMapper();
    private final String baseUrl;

    public MailHogClient() {
        this(resolveBaseUrl());
    }

    public MailHogClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    // ===== 既存のメソッド（必要に応じて既存実装とマージ） =====

    /** 全メッセージ削除 (/api/v1/messages)。 */
    public void purgeAll() {
        var req = HttpRequest.newBuilder(URI.create(baseUrl + "/api/v1/messages"))
                .timeout(Duration.ofSeconds(10)).DELETE().build();
        send(req, 200);
    }

    /** 最新本文（text/plain 優先）を取得（宛先・件名でフィルタ）。 */
    public Optional<String> getLatestBody(String toContains, String subjectContains) {
        return findLatest(toContains, subjectContains).map(this::extractBodyPreferText);
    }

    /** 条件に合う最新1件のJSONを返す。 */
    public Optional<JsonNode> findLatest(String toContains, String subjectContains) {
        JsonNode root = getMessages(100);
        JsonNode items = root.get("items");
        if (items == null || !items.isArray()) return Optional.empty();
        for (JsonNode msg : items) {
            String subj = safeText(msg.at("/Content/Headers/Subject/0"));
            String toHeader = safeText(msg.at("/Content/Headers/To/0"));
            String toAddr = !toHeader.isBlank() ? toHeader : safeText(msg.at("/MIME/Part/Headers/To/0"));
            if (toAddr.contains(toContains) && subj.contains(subjectContains)) {
                return Optional.of(msg);
            }
        }
        return Optional.empty();
    }

    // ===== 追加: テストが呼ぶエイリアス/ユーティリティ =====

    /** テスト互換: purge() -> purgeAll() のエイリアス。 */
    public void purge() {
        purgeAll();
    }

    /**
     * 受信件名の件数が min 以上になるまでポーリング。
     * @param min 最低件数
     * @param interval ポーリング間隔
     * @param timeout タイムアウト
     * 例: waitUntilSubjectsAtLeast(1, Duration.ofMillis(250), Duration.ofSeconds(10))
     */
    public void waitUntilSubjectsAtLeast(int min, Duration interval, Duration timeout) {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            int count = countMessages();
            if (count >= min) return;
            sleep(interval);
        }
        throw new IllegalStateException("Timed out waiting for messages >= " + min);
    }

    /** 最新から limit 件の件名を返す。 */
    public List<String> subjects(int limit) {
        JsonNode root = getMessages(Math.max(limit, 1));
        JsonNode items = root.get("items");
        List<String> out = new ArrayList<>();
        if (items != null && items.isArray()) {
            for (int i = 0; i < items.size() && out.size() < limit; i++) {
                JsonNode msg = items.get(i);
                out.add(safeText(msg.at("/Content/Headers/Subject/0")));
            }
        }
        return out;
    }

    /** 最新メールの本文を返す（フィルタ無し）。見つからなければ例外。 */
    public String lastBody() {
        JsonNode latest = getLatestRaw().orElseThrow(() -> new IllegalStateException("No messages"));
        return extractBodyPreferText(latest);
    }

    // ===== 内部実装 =====

    private Optional<JsonNode> getLatestRaw() {
        JsonNode root = getMessages(1);
        JsonNode items = root.get("items");
        if (items != null && items.isArray() && items.size() > 0) {
            return Optional.of(items.get(0));
        }
        return Optional.empty();
    }

    private int countMessages() {
        JsonNode root = getMessages(1); // v2/messages は total を返す
        JsonNode total = root.get("total");
        return (total != null && total.isInt()) ? total.asInt() : // v2 互換
                (root.get("count") != null ? root.get("count").asInt() : // 実装差異フォールバック
                        sizeOf(root.get("items")));
    }

    private int sizeOf(JsonNode n) {
        return (n != null && n.isArray()) ? n.size() : 0;
        }

    private JsonNode getMessages(int limit) {
        var req = HttpRequest.newBuilder(URI.create(baseUrl + "/api/v2/messages?limit=" + limit))
                .timeout(Duration.ofSeconds(10)).GET().build();
        String body = send(req, 200);
        try { return om.readTree(body); }
        catch (Exception e) { throw new IllegalStateException("MailHog API parse error", e); }
    }

    private String extractBodyPreferText(JsonNode msg) {
        String text = safeText(msg.at("/Content/Body"));
        if (!text.isBlank()) return text;
        JsonNode parts = msg.at("/MIME/Parts");
        if (parts != null && parts.isArray()) {
            String html = null;
            for (JsonNode p : parts) {
                String ctype = safeText(p.at("/Headers/Content-Type/0")).toLowerCase();
                String body = safeText(p.get("Body"));
                if (ctype.contains("text/plain")) return body;
                if (ctype.contains("text/html")) html = body;
            }
            if (html != null) return html.replaceAll("<[^>]+>", "");
        }
        return text;
    }

    private String send(HttpRequest req, int expected) {
        try {
            var res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() != expected) {
                throw new IllegalStateException("MailHog API status " + res.statusCode() + " uri=" + req.uri() + " body=" + res.body());
            }
            return res.body();
        } catch (Exception e) {
            throw new IllegalStateException("MailHog API request failed: " + req.uri(), e);
        }
    }

    private static String safeText(JsonNode n) {
        return (n == null || n.isMissingNode() || n.isNull()) ? "" : n.asText("");
    }

    private static String resolveBaseUrl() {
        String v = System.getProperty(SYS_PROP);
        if (v == null || v.isBlank()) v = System.getenv(ENV);
        if (v == null || v.isBlank()) v = "http://localhost:8025";
        return v;
    }

    private static void sleep(Duration d) {
        try { Thread.sleep(Math.max(1, d.toMillis())); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}

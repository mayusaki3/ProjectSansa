[目次](../../目次.md) > 共通仕様 > ロギング仕様 > sansa-auth適用ガイド

# 04_sansa-auth 適用ガイド  
（Project Sansa 共通ロギング仕様のサービス実装への適用）

## 1. 目的

本書は、共通ロギング仕様（01〜03）を **sansa-auth** に適用する際の  
実装方針・ポイント・禁止事項・例外との連携ルールをまとめたガイドである。

対象サービス：  
- sansa-auth（認証・認可・MFA・WebAuthn・Session 管理）

---

## 2. ログの種類と出力タイミング

### 2.1 出力するログ種別

| 種別 | 用途 | sansa-auth での主な出力箇所 |
|------|------|------------------------------|
| **監査ログ（audit）** | ユーザ動作の記録 | Login, Logout, MFA verify, Token refresh など |
| **システムログ（system）** | 内部状態・障害の記録 | ポート実装・外部通信・例外捕捉・WARN など |

---

## 3. 監査ログの出力ルール（sansa-auth）

### 3.1 出力すべきイベント一覧（必須）

| eventType | タイミング | result |
|-----------|------------|--------|
| LOGIN_SUCCESS | 認証成功 | SUCCESS |
| LOGIN_FAILED | 認証失敗 | FAILURE |
| LOGOUT | ログアウト要求受領後 | SUCCESS |
| MFA_SEND | MFA コード送信 | SUCCESS |
| MFA_VERIFY_SUCCESS | 多要素認証成功 | SUCCESS |
| MFA_VERIFY_FAILED | 多要素認証失敗 | FAILURE |
| TOKEN_REFRESH | Refresh Token による再発行 | SUCCESS / FAILURE |
| SESSION_INVALIDATED | セッション削除 | SUCCESS |

### 3.2 出力禁止事項

- 例外メッセージ全文  
- 認証トークン（Access / Refresh）  
- MFA コードそのもの  
- 生 IP / 生 User-Agent  
- 詳細な stackTrace（監査ログには含めない）

---

## 4. システムログの出力ルール（sansa-auth）

### 4.1 想定出力箇所

| 箇所 | 内容 | レベル |
|------|------|--------|
| AuthServiceImpl | 外部ポート（PasswordPort / TokenIssuer）での失敗 | WARN / ERROR |
| MfaService | メール送信失敗など | WARN / ERROR |
| WebAuthnService | デバイス検証失敗 | WARN / ERROR |
| SessionService | セッション永続化の不整合 | ERROR |
| ControllerAdvice | 予期せぬ例外 | ERROR |

### 4.2 システムログには以下を含めてもよい

- 例外クラス名  
- stackTrace（sanitize 後）  
- 外部 API の応答コード（秘匿情報は除外）  
- 自動再試行の情報（attempt 回数）  

### 4.3 禁止事項

- 個人情報（メール・UserId を除く）  
- トークン文字列  
- パスワード  
- デバイス公開鍵の生データ  

---

## 5. 例外処理とログの関係（sansa-auth）

### 5.1 ドメイン例外（HTTP4xx）との対応

| 例外 | 監査ログ出力 | システムログ出力 | コメント |
|------|--------------|------------------|----------|
| InvalidCredentialsException | LOGIN_FAILED | （必要なら WARN） | 監査ログ必須 |
| InvalidCodeException | MFA_VERIFY_FAILED | （必要なら WARN） | 同上 |
| InvalidTokenException | あり | WARN | Refresh 失敗 |
| TokenExpiredException | あり（refresh のみ） | WARN | 期限切れ |
| TokenReusedException | あり | ERROR | 不正使用なので高レベル |
| NotFoundException | 不要 | WARN | パスワードリセット等で使用 |
| ConflictException | 不要 | WARN | 重複登録など |
| GoneException | あり（必要に応じて） | WARN | 消滅したリソースへのアクセス |
| SessionNotFoundException | あり | INFO/WARN | 全セッション無効化操作時に発生し得る |
| RateLimitException | あり | INFO | 攻撃兆候としてメトリクス化推奨 |

### 監査ログ出力の基本ルール  
- 監査ログ **1 レコード = 1 ユースケース単位**  
- Controller → Service → Repository の深い階層から多重で出さない  
- **最終決定地点（Service 層 or Controller 層）で一度だけ出力**

---

## 6. 実装アーキテクチャにおけるログの責務分割

### 6.1 Controller（Spring MVC）

- 入出力のバリデーションエラーは監査対象外  
- 例外は握りつぶさず、ControllerAdvice に委譲  
- 認証/ログアウトなどユースケースの開始・終了で監査ログ出力を指示

### 6.2 Service（AuthServiceImpl）

- ユースケース内の成功/失敗を集約  
- 監査ログ出力の **唯一の責務を持つ層**  
- Repository や Port で例外が発生してもここでドメイン例外に変換し、監査ログへ反映

### 6.3 Port（PasswordPort / TokenIssuer / CurrentUserPort）

- システムログ対象（WARN/ERROR）  
- 監査ログを出力してはならない  
- 機密情報をログに書き込まないようにする

---

## 7. ログ出力 API の設計指針（sansa-auth）

### 7.1 推奨インターフェース（サンプル）

```java
public interface AuditLogger {
    void log(AuditRecord record);
}

public interface SystemLogger {
    void info(String message, Map<String, Object> detail);
    void warn(String message, Map<String, Object> detail);
    void error(String message, Throwable ex, Map<String, Object> detail);
}
```

### 7.2 sansa-auth の利用ポイント

- Service 層では `AuditLogger` を直接利用  
- Port 層では `SystemLogger` を利用し、異常系を記録  
- Controller 層は基本的にログを出さない（必要なら system のみ）  

---

## 8. sansa-auth JSON 形式（監査ログの例）

```json
{
  "eventType": "LOGIN_FAILED",
  "actorUserId": null,
  "result": "FAILURE",
  "errorCode": "INVALID_CREDENTIALS",
  "clientIpHash": "f22ac1...",
  "userAgentHash": "92acff..."
}
```

---

## 9. 今後の拡張（例）

- ログの永続化先を Cassandra 共通 Keyspace に統合  
- API シグネチャ整合性チェック（署名 + kid）  
- ログレベルのルールをポリシー化  
- セキュリティインシデント検知ルールの適用

---

## 10. 関連ドキュメント

- [ロギングポリシー](./01_ロギングポリシー.md)
- [監査ログポリシー](./02_監査ログポリシー.md)
- [ログスキーマ共通定義](./03_ログスキーマ共通定義.md)

---
[目次](../../目次.md) > 共通仕様 > ロギング仕様 > sansa-auth適用ガイド

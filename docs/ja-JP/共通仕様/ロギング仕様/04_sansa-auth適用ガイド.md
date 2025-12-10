[目次](../../目次.md) > 共通仕様 > ロギング仕様 > sansa-auth適用ガイド

# 04_sansa-auth 適用ガイド  
（Project Sansa 共通ロギング仕様のサービス実装への適用）

## 1. 目的

本書は、共通ロギング仕様（01〜03）を **sansa-auth** に適用する際の  
実装方針・責務分離・例外との連携ルールをまとめたガイドである。

本ガイドは以下を前提とする。

- ログ仕様は Project Sansa 全体で共通
- sansa-auth は「認証・セッション」という高リスク領域である
- **DomainException 設計を前提にロギング実装を行う**

対象サービス：  
- sansa-auth（認証・認可・MFA・WebAuthn・Session 管理）

---

## 2. ログの種類と出力タイミング

### 2.1 出力するログ種別

| 種別 | 用途 | sansa-auth での主な出力箇所 |
|------|------|------------------------------|
| **監査ログ（audit）** | ユーザ操作・結果の記録 | Login / Logout / MFA / Token Refresh |
| **システムログ（system）** | 内部状態・障害・異常検知 | Port 層 / Service 層 / 例外捕捉 |

### 補足  
- **監査ログは「ユーザ行為と結果」を記録するものであり、例外詳細のログではない**
- システムログは、障害解析・運用監視を目的とする

---

## 3. 監査ログの出力ルール（sansa-auth）

### 3.1 出力すべきイベント一覧（必須）

| eventType | タイミング | result |
|-----------|------------|--------|
| LOGIN_SUCCESS | 認証成功 | SUCCESS |
| LOGIN_FAILED | 認証失敗 | FAILURE |
| LOGOUT | ログアウト処理完了 | SUCCESS |
| MFA_SEND | MFA コード送信 | SUCCESS |
| MFA_VERIFY_SUCCESS | 多要素認証成功 | SUCCESS |
| MFA_VERIFY_FAILED | 多要素認証失敗 | FAILURE |
| TOKEN_REFRESH | Refresh Token による再発行 | SUCCESS / FAILURE |
| SESSION_INVALIDATED | セッション削除 | SUCCESS |

### 3.2 出力禁止事項（再確認）

- 例外の message / stackTrace  
- 認証トークン（Access / Refresh）  
- MFA コードそのもの  
- 生 IP / 生 User-Agent  
- 内部識別子（tokenId, tv 等）の直接出力  

---

## 4. システムログの出力ルール（sansa-auth）

### 4.1 想定出力箇所

| 箇所 | 内容 | レベル |
|------|------|--------|
| AuthServiceImpl | Port / Repository 失敗 | WARN / ERROR |
| MfaService | メール送信失敗 | WARN / ERROR |
| WebAuthnService | デバイス検証失敗 | WARN / ERROR |
| SessionService | 永続化不整合 | ERROR |
| ExceptionHandler | 想定外例外 | ERROR |

### 補足

- システムログは **例外型・errorCode・状況** を記録する
- DomainException の message は使用しない
- 本番では stackTrace 出力の可否を設定で制御できることが望ましい

---

## 5. 例外処理とログの関係（sansa-auth）

### 5.1 DomainException とログの対応方針

| 例外 | 監査ログ | システムログ | 備考 |
|------|----------|--------------|------|
| InvalidCredentialsException | LOGIN_FAILED | WARN | 認証失敗 |
| InvalidCodeException | MFA_VERIFY_FAILED | WARN | MFA |
| InvalidTokenException | TOKEN_REFRESH(FAILURE) | WARN | Refresh |
| TokenExpiredException | TOKEN_REFRESH(FAILURE) | WARN | 期限切れ |
| TokenReusedException | TOKEN_REFRESH(FAILURE) | ERROR | セキュリティ事案 |
| NotFoundException | 原則不要 | WARN | 想定内 |
| ConflictException | 原則不要 | WARN | 重複 |
| GoneException | 必要に応じて | WARN | 期限切れ |
| SessionNotFoundException | SESSION_INVALIDATED | INFO/WARN | 管理操作 |
| RateLimitException | 原則不要 | INFO | 攻撃傾向 |

### 重要原則

- **監査ログは「例外そのもの」ではなく「ユースケースの結果」を記録**
- DomainException は監査ログ生成の *トリガ* にはなるが、内容は記録しない
- 例外の詳細（原因・スタック）はシステムログ専用

---

## 6. 実装アーキテクチャとログ責務

### 6.1 Controller

- 入力バリデーションエラーは監査対象外
- DomainException は捕捉せず上位へ伝播
- ログ出力責務を持たない（例外的に system INFO のみ可）

### 6.2 Service（重要）

- **監査ログ出力の唯一の責務を持つ**
- Port / Repository 例外を DomainException に変換
- ユースケース単位で SUCCESS / FAILURE を確定させ、監査ログを出力

### 6.3 Port / Repository

- システムログのみ
- 監査ログ出力は禁止
- 機密情報をログに含めない

---

## 7. ログ出力 API 設計指針

### 7.1 推奨インターフェース

^^^java
public interface AuditLogger {
    void log(AuditRecord record);
}

public interface SystemLogger {
    void info(String message, Map<String, Object> detail);
    void warn(String message, Map<String, Object> detail);
    void error(String message, Throwable ex, Map<String, Object> detail);
}
^^^

### 7.2 利用方針

- AuditLogger：Service 層のみ
- SystemLogger：Service / Port 層
- Controller 直接利用は禁止（将来の共通ハンドラ導入を妨げるため）

---

## 8. 監査ログ JSON 例（再掲）

^^^json
{
  "eventType": "LOGIN_FAILED",
  "actorUserId": null,
  "result": "FAILURE",
  "errorCode": "auth.invalid_credentials",
  "clientIpHash": "f22ac1...",
  "userAgentHash": "92acff..."
}
^^^

---

## 9. 将来拡張と注意点

- ControllerAdvice による **例外 → system/audit 連携**
- Cassandra 正本化
- 署名付き監査ログ（recordHash / signature）
- セキュリティイベント自動検知

---

## 10. 関連ドキュメント

- [ロギングポリシー](./01_ロギングポリシー.md)
- [監査ログポリシー](./02_監査ログポリシー.md)
- [ログスキーマ共通定義](./03_ログスキーマ共通定義.md)

---
[目次](../../目次.md) > 共通仕様 > ロギング仕様 > sansa-auth適用ガイド

[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: AuthResult / LoginResult

# DTO: AuthResult / LoginResult（Application層）

## 位置づけ
Application層（サービスI/F）で返す「結果」DTO。Controller層でREST入出力DTOに変換する。

## AuthResult
登録系・検証系の汎用結果。

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| success | boolean | 必須 | 成否 |
| nextAction | string? | 任意 | 次の期待アクション（例: "verify_email", "register_completed" など） |
| context | object? | 任意 | 次アクションに必要な最小限の情報（例: { preRegId:string }） |
| warnings | string[]? | 任意 | 補足・注意（内部的用途も可） |

### 定義: AuthResult（補足）
本DTOは処理全体の成否を単一の `success` で表すが、認証処理全体の状態をより厳密に表現するために
内部列挙 `AuthResultType` を導入する。  
※ AuthResultType は AuthResult 内部で宣言される列挙（enum）として定義する。

| 値 | 意味 | 備考 |
|---|---|---|
| SUCCESS | 認証・登録が正常に完了した状態 | RESTの authenticated=true に対応 |
| MFA_REQUIRED | 多要素認証が必要な状態 | RESTの mfaRequired=true に対応 |
| FAILED | 資格情報不正などの理由による失敗 | RESTの 401/423 に対応 |
| LOCKED | アカウントがロックされている状態 | RESTの 423 Locked に対応 |
| RATE_LIMITED | 試行回数超過などにより一時的に認証を停止 | RESTの 429 Too Many Requests に対応 |

この列挙は Application 層での内部制御用であり、Controller 層では `success` / `mfaChallenge` などに変換される。

設計メモ: RESTの応答形は各エンドポイント仕様に従いControllerで形成する。Application層ではドメイン上の次状態を `nextAction` として明示する。

### 定義: MfaInfo
MFA_REQUIRED の場合、LoginResult.mfa に本情報を格納する。

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| type | string | ✅ | チャレンジ種別。`"totp" | "email" | "recovery" | "webauthn" | "fido" | "sms" | "sansa"` |
| transactionId | string | 任意 | 再送/検証要求とのひも付け用ID（CSRF/リプレイ対策にも使用） |
| remain | int | 任意 | 残り試行回数（レート制限やロック方針に応じて省略可） |
| retryAfter | string(ISO8601) | 任意 | 次試行可能時刻（UTC, RFC3339）。例：`2025-11-21T07:12:00Z` |
| resendAvailableAt | string(ISO8601) | 任意 | コード再送可能時刻 |
| channelHint | string | 任意 | 送信先ヒント（例：`m***@example.com`） |
| hints | string[] | 任意 | UI向け補足（例：`"Authenticatorアプリを開いてください"`） |

設計メモ：  
- `remain`/`retryAfter` はレート制限連携のため。  
- `transactionId` は多要素フローのステートレス運用・監査の要。  
- `channelHint` は個人情報を直接返さないマスキング前提。  

設計メモ（将来対応：type拡張方針）:
- `"fido"`: FIDO/U2F/Passkey等の物理デバイス認証を表す。Project Sansa専用デバイス対応を想定。
- `"sms"`: 携帯電話番号宛のコード送信。外部サービス連携を前提とする。
- `"sansa"`: Project Sansa 自体がアイデンティティ・プロバイダとして機能する場合に使用。
  他サービスからの委任認証フロー（OAuth2/OIDC連携等）を想定。

## LoginResult
ログイン結果（成功時トークンを含む）。  

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| result | AuthResultType | ✅ | 認証結果状態（SUCCESS/MFA_REQUIRED/FAILED） |
| session | SessionInfo | 成功時 | セッション情報 |
| tokens | LoginTokens | 成功時 | アクセス・リフレッシュトークン |
| user | UserSummary | 成功時 | ログインユーザー概要 |
| mfa | MfaInfo | MFA_REQUIRED時 | 要素認証に必要な情報 |
| amr | string[] | 任意 | 認証手段（例: ["pwd"], ["webauthn", "mfa"]） |

### REST ↔ Application 対応表

| REST LoginResponse          | Application LoginResult                 |
|----------------------------|-----------------------------------------|
| authenticated=true         | result=SUCCESS, tokens/user/session 設定 |
| mfaRequired=true, mfa=*    | result=MFA_REQUIRED, mfa を設定         |
| 401/423 等                  | result=FAILED                           |

### 参照DTO
- LoginTokens: トークンペア（Access/Refresh）
- SessionInfo: セッション情報
- UserSummary: ユーザー概要
- MfaInfo: 要素認証（TOTP/Emailなど）

### Controller 変換（参考）
- `result=SUCCESS` → `LoginResponse{ authenticated:true, tokens, user, session, amr }`
- `result=MFA_REQUIRED` → `LoginResponse{ authenticated:false, mfaRequired:true, mfa }`
- `result=FAILED` → HTTP 401/423 + エラーDTO

設計メモ補足: Controller層では LoginResult.result を基に HTTP ステータスおよび REST 応答DTOを生成する。

## 備考:
- MFA要求時は `LoginResult.mfa` に統一して返す。
- ログアウト/全端末失効は Application層は戻り値なしで統一し、Controllerで `LogoutResponse{success}` を返す（REST統一方針）。
- AuthService I/F は既存仕様に準拠。

---
[目次](../../../../目次.md) > API仕様 > Webサービス > [認証・セッション (M01) 目次](../目次.md) > DTO: AuthResult / LoginResult

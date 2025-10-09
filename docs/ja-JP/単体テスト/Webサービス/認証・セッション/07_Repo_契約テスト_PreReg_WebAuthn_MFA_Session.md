[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 07. Repo 契約テスト

# 07. Repo 契約テスト（PreReg / VerifyCode / WebAuthn / MFA / Session）

## PreReg / VerifyCode Repo
### UT-07-001: 事前登録コードの発行・取得
- Save → Find（email+latest）
- Then: 最新を返却

### UT-07-002: TTL 経過で失効
- Given: TTL 経過
- Then: 取得不可

### UT-07-003: 消費は一度きり
- verify 成功で consume
- Then: 再取得不可

## WebAuthn Credential Repo
### UT-07-004: 登録/取得/一覧/削除
- save → findById → listByUser → delete
- Then: 期待どおり

### UT-07-005: signCount 更新
- 検証成功のたびに単調増加

## MFA Enrollment Repo
### UT-07-006: TOTP secret 保存/有効化フラグ
- enroll → activate → verify に反映

### UT-07-007: Email OTP 状態
- 発行・再送間隔・TTL の状態遷移

## Session Repo
### UT-07-008: セッション作成/列挙/削除
- create → listByUser → delete
- Then: 一貫

### UT-07-009: token_version の参照/更新
- get → increment → 反映確認

---
[目次](../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 07. Repo 契約テスト
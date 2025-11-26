[目次](../../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 07. Repo 契約テスト

# 07. Repo 契約テスト（PreReg / VerifyCode / WebAuthn / MFA / Session）

## PreReg / VerifyCode Repo
### M01:UT-07-001: 事前登録コードの発行・取得
- Save → Find（email+latest）
- Then: 最新を返却

### M01:UT-07-002: TTL 経過で失効
- Given: TTL 経過
- Then: 取得不可

### M01:UT-07-003: 消費は一度きり
- verify 成功で consume
- Then: 再取得不可

## WebAuthn Credential Repo
### M01:UT-07-004: 登録/取得/一覧/削除
- save → findById → listByUser → delete
- Then: 期待どおり

### M01:UT-07-005: signCount 更新
- 検証成功のたびに単調増加

## MFA Enrollment Repo
### M01:UT-07-006: TOTP secret 保存/有効化フラグ
- enroll → activate → verify に反映

### M01:UT-07-007: Email OTP 状態
- 発行・再送間隔・TTL の状態遷移

## Session Repo
### M01:UT-07-008: セッション作成/列挙/削除
- create → listByUser → delete
- Then: 一貫

### M01:UT-07-009: token_version の参照/更新
- get → increment → 反映確認

## MFA/TOTP ストア契約
### M01:UT-07-010: TOTPパラメータの永続化
- Save: secret,algorithm,digits,period
- Load: 保存した値がそのまま取得できる

### M01:UT-07-011: 既定値保存の明示／暗黙
- 指定なし → 既定値で保存（後続の検証でURL/検証器へ反映）

### M01:UT-07-012: マイグレーション互換
- 旧データ（パラメータ欠落）読み出し時のデフォルト補完

---
[目次](../../../../目次.md) > 単体テスト > Webサービス > [認証・セッション 単体テスト 目次](目次.md) > 07. Repo 契約テスト
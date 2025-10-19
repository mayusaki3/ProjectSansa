-- Keyspace
CREATE KEYSPACE IF NOT EXISTS sansa_auth
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
  AND durable_writes = true;

-- Users（ユーザー）
CREATE TABLE IF NOT EXISTS sansa_auth.users (
  user_id     uuid PRIMARY KEY,
  account_id  text,
  email       text,
  status      text,        -- ACTIVE / PENDING / LOCKED / DELETED
  password    text,        -- ハッシュ（PBKDF2/BCrypt/Argon2 等）
  locale      text,        -- 既定言語
  created_at  timestamp,
  updated_at  timestamp
);

-- 逆引き（account_id → user）
CREATE TABLE IF NOT EXISTS sansa_auth.users_by_account (
  account_id  text PRIMARY KEY,
  user_id     uuid
);

-- 逆引き（email → user）
CREATE TABLE IF NOT EXISTS sansa_auth.users_by_email (
  email       text PRIMARY KEY,
  user_id     uuid,
  domain      text        -- ドメインブロック/統計用
);

-- Sessions（セッション）
CREATE TABLE IF NOT EXISTS sansa_auth.sessions (
  session_id    uuid PRIMARY KEY,
  user_id       uuid,
  device_id     text,
  ip            text,
  ua            text,
  issued_at     timestamp,
  expires_at    timestamp,      -- 参照用（ソフト制御）
  refresh_token text            -- 失効時の照合用（必要なら暗号化）
);

-- ユーザー別にセッションを列挙/一括削除するためのビュー
CREATE TABLE IF NOT EXISTS sansa_auth.sessions_by_user (
  user_id       uuid,
  device_id     text,
  session_id    uuid,
  issued_at     timestamp,
  PRIMARY KEY ((user_id), device_id, session_id)
) WITH CLUSTERING ORDER BY (device_id ASC, session_id DESC);

-- Email Verification（メール認証）
CREATE TABLE IF NOT EXISTS sansa_auth.email_verifications (
  challenge_id  uuid PRIMARY KEY,
  email         text,
  account_id    text,
  code          text,
  created_at    timestamp
) WITH default_time_to_live = 900; -- 15分

-- MFA TOTP 状態
CREATE TABLE IF NOT EXISTS sansa_auth.mfa_totp (
  user_id     uuid PRIMARY KEY,
  secret      text,
  enabled     boolean,
  updated_at  timestamp
);

-- MFA 一時的なメールMFA切替フラグ（TTLで戻る）
CREATE TABLE IF NOT EXISTS sansa_auth.mfa_temp_switch (
  user_id     uuid PRIMARY KEY,
  method      text,       -- email
  created_at  timestamp
) WITH default_time_to_live = 86400; -- 24h

-- auth_audit（監査ログ）
CREATE TABLE IF NOT EXISTS sansa_auth.auth_audit (
  user_id   uuid,
  yyyymmdd  text,       -- 日単位パーティション（例: 2025-10-04）
  ts        timeuuid,   -- 時系列（降順取得用に timeuuid）
  action    text,       -- REGISTER_START / LOGIN_OK / LOGOUT_ALL / ...
  ip        text,
  ua        text,
  result    text,       -- OK / NG / REJECTED など
  detail    text,
  PRIMARY KEY ((user_id, yyyymmdd), ts)
) WITH CLUSTERING ORDER BY (ts DESC);

-- Rate Limit（レート制限）
CREATE TABLE IF NOT EXISTS sansa_auth.rate_limits (
  bucket      text,    -- 'login_fail','email_send' 等
  key         text,    -- 'userId:ip' 等の複合キー文字列
  window_start timestamp,
  count       int,
  PRIMARY KEY ((bucket, key), window_start)
) WITH default_time_to_live = 1200; -- 20分

-- blocklists（ドメイン）
CREATE TABLE IF NOT EXISTS sansa_auth.block_domains (
  domain text PRIMARY KEY,
  reason text,
  created_at timestamp
);

-- blocklists（アカウント）
CREATE TABLE IF NOT EXISTS sansa_auth.block_accounts (
  account_id text PRIMARY KEY,
  reason text,
  created_at timestamp
);

-- password_resets（再設定フロー）
CREATE TABLE IF NOT EXISTS sansa_auth.password_resets (
  request_id   uuid PRIMARY KEY,
  user_id      uuid,
  email        text,
  code         text,        -- またはトークン（ハッシュ保存推奨）
  created_at   timestamp
) WITH default_time_to_live = 1800; -- 30分

-- token_blacklist（失効トークン）
CREATE TABLE IF NOT EXISTS sansa_auth.token_blacklist (
  jti         text PRIMARY KEY,   -- JWT ID など一意識別子
  user_id     uuid,
  reason      text,
  created_at  timestamp
) WITH default_time_to_live = 604800; -- 7日

-- users_security（ロック/履歴）
CREATE TABLE IF NOT EXISTS sansa_auth.users_security (
  user_id     uuid PRIMARY KEY,
  locked      boolean,       -- ロック状態
  locked_at   timestamp,
  reason      text,
  updated_at  timestamp,
  pw_hist     list<text>     -- 過去N件のパスワードハッシュ（履歴禁止用）
);

-- email_change_requests（メール変更）
CREATE TABLE IF NOT EXISTS sansa_auth.email_change_requests (
  request_id   uuid PRIMARY KEY,
  user_id      uuid,
  old_email    text,
  new_email    text,
  code         text,
  created_at   timestamp
) WITH default_time_to_live = 1800; -- 30分

-- device_trusts（信頼デバイス）
CREATE TABLE IF NOT EXISTS sansa_auth.device_trusts (
  user_id     uuid,
  device_id   text,
  trusted_at  timestamp,
  expires_at  timestamp,
  PRIMARY KEY ((user_id), device_id)
);

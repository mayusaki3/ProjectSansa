-- Keyspace is created by CassandraConfig

CREATE TABLE IF NOT EXISTS users (
  user_id uuid PRIMARY KEY,
  account_id text,
  email text,
  email_verified boolean,
  mfa_enabled boolean,
  language text,
  token_version bigint,
  created_at timestamp,
  last_login_at timestamp
);

CREATE TABLE IF NOT EXISTS users_by_email (
  email text PRIMARY KEY,
  user_id uuid,
  account_id text
);

CREATE TABLE IF NOT EXISTS sessions (
  session_id uuid PRIMARY KEY,
  user_id uuid,
  device_id text,
  token_version bigint,
  created_at timestamp,
  last_seen_at timestamp,
  mfa_last_ok_at timestamp
);

CREATE TABLE IF NOT EXISTS sessions_by_user (
  user_id uuid,
  session_id uuid,
  device_id text,
  token_version bigint,
  created_at timestamp,
  last_seen_at timestamp,
  PRIMARY KEY (user_id, session_id)
) WITH CLUSTERING ORDER BY (session_id DESC);

CREATE TABLE IF NOT EXISTS sessions_by_id (
  session_id uuid PRIMARY KEY,
  user_id uuid,
  device_id text
);

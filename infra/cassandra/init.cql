CREATE KEYSPACE IF NOT EXISTS sansa
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
    };

-- ユーザー
CREATE TABLE IF NOT EXISTS sansa.users (
    user_id text PRIMARY KEY,
    username text,
    password_hash text,
    display_name text,
    created_at timestamp
);

CREATE TABLE IF NOT EXISTS sansa.users_by_username (
    username text PRIMARY KEY,
    user_id text,
    password_hash text,
    display_name text,
    created_at timestamp
);

-- 投稿
CREATE TABLE IF NOT EXISTS sansa.posts_by_time (
    bucket_day date,
    created_at timeuuid,
    post_id uuid,
    author_id text,
    text text,
    media list<text>,
    lang text,
    PRIMARY KEY ((bucket_day), created_at)
) WITH CLUSTERING ORDER BY (created_at DESC);

CREATE TABLE IF NOT EXISTS sansa.posts_by_user (
    author_id text,
    created_at timeuuid,
    post_id uuid,
    text text,
    PRIMARY KEY ((author_id), created_at)
) WITH CLUSTERING ORDER BY (created_at DESC);

CREATE TABLE IF NOT EXISTS sansa.replies_by_post (
    post_id uuid,
    created_at timeuuid,
    reply_id uuid,
    author_id text,
    text text,
    PRIMARY KEY ((post_id), created_at)
) WITH CLUSTERING ORDER BY (created_at DESC);

CREATE TABLE IF NOT EXISTS sansa.likes_by_post (
    post_id uuid,
    user_id text,
    PRIMARY KEY ((post_id), user_id)
);

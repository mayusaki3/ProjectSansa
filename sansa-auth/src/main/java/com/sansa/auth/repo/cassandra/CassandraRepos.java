package com.sansa.auth.repo.cassandra;

import com.sansa.auth.model.Models;
import com.sansa.auth.repo.RepoInterfaces;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 既存のCassandra実装に最小影響で、インタフェース変更に追従するための薄いラッパ。
 * 既存のメソッド群（findById, findByEmail, save, deleteById 等）はそのまま利用してください。
 * ここで追加したメソッドは、既存のクエリがある場合はそこへ委譲、無い場合は安全なフォールバックにしています。
 */
public final class CassandraRepos {

    @Repository("cassandraRepos.UserRepo")
    public static class UserRepo implements RepoInterfaces.IUserRepo {

        // 既存のフィールド・コンストラクタ・CQLステートメントはそのまま残してください。
        // private final CqlSession session; ... 等

        @Override
        public Optional<Models.User> findById(UUID id) {
            // 既存実装に置き換えてください
            throw new UnsupportedOperationException("findById(UUID) is not wired here. Use existing implementation.");
        }

        @Override
        public Optional<Models.User> findByEmail(String email) {
            // 既存実装に置き換えてください
            throw new UnsupportedOperationException("findByEmail(String) is not wired here. Use existing implementation.");
        }

        @Override
        public Models.User save(Models.User user) {
            // 既存実装に置き換えてください
            throw new UnsupportedOperationException("save(User) is not wired here. Use existing implementation.");
        }

        @Override
        public void deleteById(UUID id) {
            // 既存実装に置き換えてください
            throw new UnsupportedOperationException("deleteById(UUID) is not wired here. Use existing implementation.");
        }

        // 追加：loginId検索（既存に無ければ email を loginId とみなす）
        @Override
        public Optional<Models.User> findByLoginId(String loginId) {
            try {
                // loginId が UUID 形式なら id として探す
                UUID asUuid = UUID.fromString(loginId);
                Optional<Models.User> byId = findById(asUuid);
                if (byId.isPresent()) return byId;
            } catch (IllegalArgumentException ignore) { }
            // それ以外は email として検索（実要件に合わせて修正）
            return findByEmail(loginId);
        }

        // 追加：existsById（UUID化して findById 判定）
        @Override
        public boolean existsById(String id) {
            try {
                return findById(UUID.fromString(id)).isPresent();
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        // 追加：existsByEmail（findByEmail 判定）
        @Override
        public boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }
    }

    @Repository("cassandraRepos.SessionRepo")
    public static class SessionRepo implements RepoInterfaces.ISessionRepo {

        // 既存のフィールド・コンストラクタ・CQLステートメントはそのまま残してください。

        @Override
        public Optional<Models.Session> findById(UUID id) {
            // 既存実装に置き換えてください
            throw new UnsupportedOperationException("findById(UUID) is not wired here. Use existing implementation.");
        }

        @Override
        public Models.Session save(Models.Session session) {
            // 既存実装に置き換えてください
            throw new UnsupportedOperationException("save(Session) is not wired here. Use existing implementation.");
        }

        @Override
        public void deleteById(UUID id) {
            // 既存実装に置き換えてください
            throw new UnsupportedOperationException("deleteById(UUID) is not wired here. Use existing implementation.");
        }

        // 追加：ユーザー全セッション削除（実装があるなら置き換えてください）
        @Override
        public void deleteAllByUserId(UUID userId) {
            // 既存の DELETE FROM ... WHERE user_id=? を呼ぶ想定
            throw new UnsupportedOperationException("deleteAllByUserId(UUID) is not wired here. Use existing implementation.");
        }

        // 任意：deviceId 検索（実装があるなら置き換えてください）
        @Override
        public Optional<Models.Session> findByDeviceId(String deviceId) {
            // 既存 SELECT ... WHERE device_id=? を呼ぶ想定
            return Optional.empty();
        }
    }
}

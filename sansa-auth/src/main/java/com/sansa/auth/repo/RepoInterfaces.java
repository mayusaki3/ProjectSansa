package com.sansa.auth.repo;

import com.sansa.auth.model.Models;

import java.util.Optional;
import java.util.UUID;

/**
 * リポジトリのインタフェース郡。
 * 既存実装（Cassandra / InMemory）に影響最小で、ServicesCassandra が呼ぶメソッドを満たします。
 * 新規追加メソッドは「default 実装」を持たせ、既存実装が未対応でもコンパイルを通せるようにします。
 */
public final class RepoInterfaces {

    public interface IUserRepo {
        // 既存で多く使われる基本操作（実装側が既に持っている前提）
        Optional<Models.User> findById(UUID id);
        Optional<Models.User> findByEmail(String email);
        Models.User save(Models.User user);
        void deleteById(UUID id);

        // 追加: loginId での検索（未実装でもビルド可にするため default で空）
        default Optional<Models.User> findByLoginId(String loginId) {
            return Optional.empty();
        }

        // 追加: 文字列IDの存在確認（UUID前提の findById を使って判定）
        default boolean existsById(String id) {
            try {
                return findById(UUID.fromString(id)).isPresent();
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        // 追加: Emailの存在確認（実装が findByEmail を持っていれば true/false 判定できる）
        default boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }
    }

    public interface ISessionRepo {
        Optional<Models.Session> findById(UUID id);
        Models.Session save(Models.Session session);
        void deleteById(UUID id);

        // 追加: ユーザー全セッション削除（未実装でもビルド可）
        default void deleteAllByUserId(UUID userId) {
            // default no-op
        }

        // 任意: deviceId 検索が必要になった場合に備え default 追加
        default Optional<Models.Session> findByDeviceId(String deviceId) {
            return Optional.empty();
        }
    }
}

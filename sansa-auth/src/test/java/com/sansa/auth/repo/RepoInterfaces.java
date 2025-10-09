package com.sansa.auth.repo;

import java.util.List;
import java.util.Optional;

// ドメインモデル（存在するクラス名に合わせてください）
import com.sansa.auth.model.User;
import com.sansa.auth.model.Session;
import com.sansa.auth.model.PreReg;

/**
 * 旧テスト資産が参照しているネスト型リポジトリIFの互換シム。
 * 新実装（service/store や cassandra 実装）には影響しない。
 */
public final class RepoInterfaces {

  private RepoInterfaces() {}

  /** ユーザー系 */
  public interface IUserRepo {
    Optional<User> findById(String userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByAccountId(String accountId);

    boolean existsByEmail(String email);

    boolean existsByAccountId(String accountId);

    User save(User user);

    void deleteById(String userId);
  }

  /** セッション系 */
  public interface ISessionRepo {
    Optional<Session> findById(String sessionId);

    List<Session> findByUserId(String userId);

    Session save(Session session);

    void deleteById(String sessionId);

    void deleteByUserId(String userId);

    /** user の全セッションから exceptSessionId 以外を無効化する等の用途 */
    default void revokeAllForUserExcept(String userId, String exceptSessionId) {
      // シムのためデフォルト空実装（実装層が必要なら各実装でoverride）
    }
  }

  /** 事前登録（メール検証）系 */
  public interface IPreRegRepo {
    PreReg save(PreReg preReg);

    Optional<PreReg> findById(String preRegId);

    Optional<PreReg> findByEmail(String email);

    void deleteById(String preRegId);

    /** 期限切れ掃除用 */
    default int deleteExpired(long nowEpochMillis) {
      return 0; // シムのためデフォルト実装
    }
  }
}

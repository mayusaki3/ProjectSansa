package com.sansa.auth.repo;

import com.sansa.auth.model.PreReg;
import com.sansa.auth.model.Session;
import com.sansa.auth.model.User;
import java.util.List;
import java.util.Optional;

/**
 * 旧テスト資産が参照していた「ネスト型リポジトリIF」の互換シム。
 *
 * 目的: - 既存テストコードを大きく変更せずに、リポジトリ契約テストを記述できるようにする。 - 実装は別パッケージ（本番の Store
 * 実装等）で自由に行える。
 *
 * 注意: - あくまでテスト用のインターフェース定義。永続化の詳細は含めない。 - 必要最小限のメソッドのみ定義し、デフォルト実装は設けない。
 */
public final class RepoInterfaces {

    private RepoInterfaces() {
    }

    /**
     * User リポジトリの契約
     */
    public interface UserRepo {

        User save(User user);

        Optional<User> findById(String accountId);

        Optional<User> findByEmail(String email);

        /**
         * ログイン用の外部ID（例: "email","webauthn","github" など）で検索する想定
         */
        Optional<User> findByIdentifier(String idType, String identifierValue);

        void deleteById(String accountId);
    }

    /**
     * Session リポジトリの契約
     */
    public interface SessionRepo {

        Session save(Session session);

        Optional<Session> findById(String sessionId);

        List<Session> findByAccountId(String accountId);

        void deleteById(String sessionId);

        /**
         * アカウント配下の全セッション失効（ログアウト・オール）
         */
        int deleteByAccountId(String accountId);

        /**
         * TTL 失効掃除（現時点の時刻を渡してガベコレ）
         */
        int deleteExpired(long nowEpochMillis);
    }

    /**
     * PreReg（仮登録）リポジトリの契約
     */
    public interface PreRegRepo {

        PreReg save(PreReg preReg);

        Optional<PreReg> findById(String preRegId);

        Optional<PreReg> findByEmail(String email);

        void deleteById(String preRegId);

        /**
         * 期限切れ掃除用（テストでは戻り値で削除件数を検証可能）
         */
        int deleteExpired(long nowEpochMillis);
    }

    /**
     * WebAuthn 認証器の契約（必要最小限） - ContractRepoTest の WebAuthn 部分で利用予定
     */
    public interface WebAuthnCredentialRepo {

        /**
         * サマリ保存（本番は CredentialId/PublicKey など詳細を持つ想定）
         */
        void save(String accountId, String credentialId, long signCount);

        /**
         * アカウント配下の認証器一覧
         */
        List<String> listIds(String accountId);

        /**
         * signCount を更新（monotonic の検証に使用）
         */
        void updateSignCount(String accountId, String credentialId, long newSignCount);

        /**
         * 認証器の削除
         */
        void delete(String accountId, String credentialId);
    }

    /**
     * トークンバージョンの契約（logout_all を簡易に実現するためのカウンタ）
     */
    public interface TokenVersionRepo {

        /**
         * 現在値を取得（未作成なら 0 を想定）
         */
        int get(String accountId);

        /**
         * インクリメントして新しい値を返す
         */
        int incrementAndGet(String accountId);
    }
}

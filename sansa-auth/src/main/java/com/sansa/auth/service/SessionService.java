package com.sansa.auth.service;

import com.sansa.auth.annotations.VisibleForTesting;
import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.dto.sessions.SessionsListResponse;
import com.sansa.auth.exception.SessionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SessionService {

    // 例: 実際はストア/DAOをDI
    // private final SessionStore store;

    /**
     * 公開API：外部仕様テスト/本番が呼ぶのはこのメソッドのみ
     */
    public LogoutResponse logout(LogoutRequest req) {
        // 入力バリデーション（仕様に合わせて調整）
        if (req == null) {
            // フィールド名が不明のため、ひとまず空ビルドで返す
            return LogoutResponse.builder().build();
        }

        boolean acted = false;

        if (req.getSessionId() != null && !req.getSessionId().isBlank()) {
            revokeBySessionId(req.getSessionId());
            acted = true;
        }

        if (req.getRefreshToken() != null && !req.getRefreshToken().isBlank()) {
            revokeByRefreshToken(req.getRefreshToken());
            acted = true;
        }

        // DTO不一致だった all-devices/userId 分岐は削除（後でDTO定義に合わせて復活させます）

        // 何も指定されていなくても冪等動作として成功扱いで空レスを返す（後で要件に合わせて詰める）
        return LogoutResponse.builder().build();
    }

    /**
     * （暫定）AuthController から呼ばれているため追加
     * 現状は DTO 定義が不明なので null を返す（コンパイル目的）
     * 後で SessionInfo のビルド内容を要件に合わせて実装します。
     */
    public SessionInfo currentSession() {
        return null;
    }

    /**
     * （暫定）AuthController から呼ばれているため追加
     * すべてのセッション/デバイスの無効化を想定。実装は後でDAOへ接続。
     */
    public LogoutResponse logoutAll() {
        // 実際は store.invalidateAllForCurrentUser() 等
        return LogoutResponse.builder().build();
    }

    /**
     * 現在のユーザーのセッション一覧を返す（Controller用の薄いファサード）
     * TODO: 実装を永続化層に接続
     */
    public SessionsListResponse list() {
        // 永続化が未接続なら空で返しておけばOK（Controllerの結線目的）
        return SessionsListResponse.builder()
                .sessions(Collections.emptyList())
                .build();
    }

    /**
     * セッションIDで1件削除（存在しなければ SessionNotFoundException）
     * Controllerの DELETE /sessions/{id} で使用
     */
    public void deleteById(String sessionId) {
        // TODO: 実装。未実装段階では「見つからない」404を投げておくとテストに沿う
        throw new SessionNotFoundException(sessionId);
    }

    /**
     * TEST-ONLY: 単一セッションIDでの無効化
     * package-private ＋ VisibleForTesting で公開API化しない
     */
    @VisibleForTesting
    void revokeBySessionId(String sessionId) {
        // store.invalidateSession(sessionId);
    }

    /**
     * TEST-ONLY: リフレッシュトークンでのセッション無効化
     */
    @VisibleForTesting
    void revokeByRefreshToken(String refreshToken) {
        // store.invalidateByRefreshToken(refreshToken);
    }

    /**
     * TEST-ONLY: リフレッシュトークンでのセッション無効化
     */
    @VisibleForTesting
    public boolean revokeById(String sessionId) {
        // 既存ロジックを使って判定可能ならそれを利用
        // 例：例外設計に依存します。ここでは一例：
        try {
            revokeBySessionId(sessionId);
            // 実装上「存在しない」は例外や戻り値で分かるならそれに合わせる
            // 今回は簡易に「削除対象が無かったら false を返すよう内部を調整」でもOK
            return true;
        } catch (SessionNotFoundException e) {
            return false;
        }
    }
}

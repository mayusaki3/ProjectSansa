package com.sansa.auth.service.impl;

import com.sansa.auth.dto.auth.PreRegisterRequest;
import com.sansa.auth.dto.auth.PreRegisterResponse;
import com.sansa.auth.dto.auth.RegisterRequest;
import com.sansa.auth.dto.auth.RegisterResponse;
import com.sansa.auth.dto.auth.VerifyEmailRequest;
import com.sansa.auth.dto.auth.VerifyEmailResponse;
import com.sansa.auth.dto.login.LoginRequest;
import com.sansa.auth.dto.login.LoginResponse;
import com.sansa.auth.dto.login.TokenRefreshRequest;
import com.sansa.auth.dto.login.TokenRefreshResponse;
import com.sansa.auth.dto.sessions.LogoutRequest;
import com.sansa.auth.dto.sessions.LogoutResponse;
import com.sansa.auth.dto.sessions.SessionInfo;
import com.sansa.auth.mail.MailComposer;
import com.sansa.auth.mail.MailService;
import com.sansa.auth.service.AuthService;
import com.sansa.auth.service.SessionService;
import com.sansa.auth.service.port.PasswordPort;
import com.sansa.auth.service.port.TokenFacade;
import com.sansa.auth.store.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 認証関連サービスの実装クラス（ダミー版）。
 *
 * <p>
 * 現時点では、AuthService のインターフェースシグネチャだけを満たし、
 * すべてのメソッドは {@link UnsupportedOperationException}（"DUMMY: ..."）を送出する。
 * </p>
 *
 * <p>
 * 後続の実装フェーズでは、各メソッドの「DUMMY」例外を検索し、
 * 実際の業務ロジック（Store / SessionService / MailService / TokenFacade 等）に
 * 差し替えていく前提とする。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /**
     * ユーザーやプレ登録情報を保持するストア。
     * ダミー段階では参照のみ（実際の処理では使用しない）。
     */
    private final Store store;

    /**
     * パスワードハッシュ化などを扱うポート。
     * ダミー段階では参照のみ。
     */
    private final PasswordPort passwordPort;

    /**
     * アクセストークン、リフレッシュトークンの発行などを担うファサード。
     * ダミー段階では参照のみ。
     */
    private final TokenFacade tokenFacade;

    /**
     * セッション管理サービス。
     * ダミー段階では参照のみ。
     */
    private final SessionService sessionService;

    /**
     * メール送信を行うサービス。
     * ダミー段階では参照のみ。
     */
    private final MailService mailService;

    /**
     * 各種メール本文の組み立てを行うコンポーネント。
     * ダミー段階では参照のみ。
     */
    private final MailComposer mailComposer;

    /**
     * ユーザー登録ステップ1: プレ登録。
     *
     * @param request メールアドレス・希望言語などのプレ登録情報
     * @return プレ登録結果
     */
    // @Override
    public PreRegisterResponse preRegister(PreRegisterRequest request) {
        // UT では「本実装でダミー例外を投げた」と見なす前提のダミー実装
        throw new UnsupportedOperationException("DUMMY: preRegister is not implemented yet.");
    }

    /**
     * ユーザー登録ステップ2: メールに記載されたコードの検証。
     *
     * @param request 検証コード等
     * @return 検証結果（成功時は preRegId 等）
     */
    // @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest request) {
        throw new UnsupportedOperationException("DUMMY: verifyEmail is not implemented yet.");
    }

    /**
     * ユーザー登録ステップ3: 本登録。
     *
     * @param request プレ登録 ID / ログイン ID / パスワードなど
     * @return 登録結果
     */
    // @Override
    public RegisterResponse register(RegisterRequest request) {
        throw new UnsupportedOperationException("DUMMY: register is not implemented yet.");
    }

    /**
     * ログイン処理。
     *
     * @param request ログイン ID・パスワードなど
     * @return ログイン結果（アクセストークン・リフレッシュトークン等）
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        throw new UnsupportedOperationException("DUMMY: login is not implemented yet.");
    }

    /**
     * リフレッシュトークンを用いたトークン再発行。
     *
     * @param request リフレッシュトークン情報
     * @return 再発行されたトークン情報
     */
    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        throw new UnsupportedOperationException("DUMMY: refresh is not implemented yet.");
    }

    /**
     * 現在のセッション情報を取得。
     *
     * @return セッション情報（ユーザー ID・期限など）
     */
    // @Override
    public SessionInfo getCurrentSession() {
        throw new UnsupportedOperationException("DUMMY: getCurrentSession is not implemented yet.");
    }

    /**
     * ログアウト（現セッション or 指定セッション）。
     *
     * @param request ログアウト対象セッションを示すリクエスト
     * @return ログアウト結果
     */
    // @Override
    public LogoutResponse logout(LogoutRequest request) {
        throw new UnsupportedOperationException("DUMMY: logout is not implemented yet.");
    }

    /**
     * すべてのセッションを無効化（全端末ログアウト）。
     *
     * @return ログアウト結果
     */
    // @Override
    public LogoutResponse logoutAll() {
        throw new UnsupportedOperationException("DUMMY: logoutAll is not implemented yet.");
    }
}

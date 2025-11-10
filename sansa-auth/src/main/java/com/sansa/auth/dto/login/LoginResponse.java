package com.sansa.auth.dto.login;

import com.sansa.auth.dto.sessions.SessionInfo;
import java.util.List;

/**
 * ログイン結果。
 */
public class LoginResponse {

    private boolean success;
    private boolean mfaRequired;
    private SessionInfo session;
    private TokenRefreshResponse.Tokens tokens;
    private List<String> roles;

    public boolean isSuccess() {
        return success;
    }

    public boolean isMfaRequired() {
        return mfaRequired;
    }

    public SessionInfo getSession() {
        return session;
    }

    public TokenRefreshResponse.Tokens getTokens() {
        return tokens;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMfaRequired(boolean mfaRequired) {
        this.mfaRequired = mfaRequired;
    }

    public void setSession(SessionInfo session) {
        this.session = session;
    }

    public void setTokens(TokenRefreshResponse.Tokens tokens) {
        this.tokens = tokens;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public static class LoginResponseBuilder {
        private final LoginResponse target = new LoginResponse();

        public LoginResponseBuilder success(boolean success) {
            target.success = success;
            return this;
        }

        public LoginResponseBuilder mfaRequired(boolean mfaRequired) {
            target.mfaRequired = mfaRequired;
            return this;
        }

        public LoginResponseBuilder session(SessionInfo session) {
            target.session = session;
            return this;
        }

        public LoginResponseBuilder tokens(TokenRefreshResponse.Tokens tokens) {
            target.tokens = tokens;
            return this;
        }

        public LoginResponseBuilder roles(List<String> roles) {
            target.roles = roles;
            return this;
        }

        public LoginResponse build() {
            return target;
        }
    }
}

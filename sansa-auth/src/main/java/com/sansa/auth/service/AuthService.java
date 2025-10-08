package com.sansa.auth.service;

import java.util.Map;
import java.util.UUID;

public interface AuthService {

    /** 事前登録: email + language を受け取り、{success, message, details} を返す */
    Map<String, Object> preRegister(String email, String language);

    /** メールコード検証: email + code を受け取り、結果を返す */
    Map<String, Object> verifyEmail(String email, String code);

    /** 本登録: preRegId + language を受け取り、結果を返す（details.user にユーザー情報） */
    Map<String, Object> register(UUID preRegId, String language);
}

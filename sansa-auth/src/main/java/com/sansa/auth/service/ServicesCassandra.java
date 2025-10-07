package com.sansa.auth.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.sansa.auth.dto.Dtos;
import com.sansa.auth.model.Models;

/**
 * Cassandra用のAuthService実装。
 * 注意: cassandraプロファイル有効時のみBean登録されます。
 * いまは骨組みだけ。実処理はcassandra接続時に実装してください。
 */
@Service
@Profile("cassandra")
public class ServicesCassandra implements AuthService {

    @Override
    public Dtos.AuthResult preRegister(String email, String language) {
        // TODO: Cassandraへpre-registerレコードを作成し、preRegIdを発行して返す
        throw new UnsupportedOperationException("Not implemented for cassandra profile yet.");
    }

    @Override
    public Dtos.AuthResult verifyEmail(String preRegId, String code) {
        // TODO: preRegId/verification codeを検証して結果を返す
        throw new UnsupportedOperationException("Not implemented for cassandra profile yet.");
    }

    @Override
    public Models.User register(String preRegId, String accountId, String language) {
        // TODO: 検証済みpreRegをユーザとして確定登録し、Userを返す
        throw new UnsupportedOperationException("Not implemented for cassandra profile yet.");
    }
}

package com.sansa.auth.repo;

import com.sansa.auth.model.Models.User;
import com.sansa.auth.model.Models.Session;

import java.util.*;

public interface RepoInterfaces {

    public interface IUserRepo {
        Optional<User> findByAccountId(String accountId);
        Optional<User> findByEmail(String email);
        User save(User u);
    }

    public interface ISessionRepo {
        Session save(Session s);
        List<Session> findByUserId(UUID userId);
        void delete(UUID sessionId);
    }
}

package com.sansa.auth.service;

import com.sansa.auth.dto.Dtos;

public interface AuthService {

    Dtos.AuthResult preRegister(String email, String language);

    Dtos.AuthResult verifyEmail(String preRegId, String code);

    Dtos.AuthResult register(String preRegId, String language);
}

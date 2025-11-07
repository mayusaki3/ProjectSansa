package com.sansa.auth.service.port.impl;

import com.sansa.auth.service.port.PasswordPort;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/** Argon2id 実装。PHC形式で保存。 */
public class PasswordPortImpl implements PasswordPort {
    private final Argon2PasswordEncoder enc;
    public PasswordPortImpl(Argon2PasswordEncoder enc) { this.enc = enc; }
    @Override public String hash(String raw) { return enc.encode(raw); }
    @Override public boolean verify(String raw, String phc) { return enc.matches(raw, phc); }
    @Override public boolean needsRehash(String phc) { return false; } // 当面固定
}

package psansa.api.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class PasswordService {
    public String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt(12)); }
    public boolean verify(String raw, String hashed) { return BCrypt.checkpw(raw, hashed); }
}

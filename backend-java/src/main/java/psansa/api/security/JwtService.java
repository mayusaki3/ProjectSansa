package psansa.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@ApplicationScoped
public class JwtService {
    private String secret = System.getProperty("psansa.jwt.secret",
        System.getenv().getOrDefault("PSANSA_JWT_SECRET", "dev-secret-change-me-32bytes-minimum-123456"));
    private String issuer = System.getProperty("psansa.jwt.issuer", "ProjectSansa");
    private String audience = System.getProperty("psansa.jwt.audience", "ProjectSansaClients");
    private int expiresMin = Integer.parseInt(System.getProperty("psansa.jwt.expires-min", "1440"));

    public String issue(String userId, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setIssuer(issuer)
            .setAudience(audience)
            .setSubject(userId)
            .addClaims(Map.of("uid", userId, "uname", username))
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(60L*expiresMin)))
            .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
            .compact();
    }
}

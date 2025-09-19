package psansa.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@ApplicationScoped
public class JwtService {
  private final Key key;
  private final String issuer;
  private final String audience;
  private final long expiresMin;

  public JwtService() {
    String secret = System.getProperty("psansa.jwt.secret",
        System.getenv().getOrDefault("PSANSA_JWT_SECRET",
            "dev-secret-change-me-32bytes-minimum-123456"));
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.issuer = System.getProperty("psansa.jwt.issuer", "ProjectSansa");
    this.audience = System.getProperty("psansa.jwt.audience", "ProjectSansaClients");
    this.expiresMin = Long.parseLong(System.getProperty("psansa.jwt.expires.min","60"));
  }

  public String issue(String userId, String username) {
    Instant now = Instant.now();
    return Jwts.builder()
        .setIssuer(issuer)
        .setAudience(audience)
        .setSubject(userId)
        .addClaims(Map.of("uid", userId, "uname", username))
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(60L * expiresMin)))
        .signWith(key)
        .compact();
  }
}

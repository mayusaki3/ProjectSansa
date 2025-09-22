package psansa.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Principal;

@Provider
@JwtSecured
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class JwtAuthFilter implements ContainerRequestFilter {
  private final Key key;
  private final String issuer;
  private final String audience;

  public JwtAuthFilter() {
    String secret = System.getProperty("psansa.jwt.secret",
        System.getenv().getOrDefault("PSANSA_JWT_SECRET",
            "dev-secret-change-me-32bytes-minimum-123456"));
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.issuer = System.getProperty("psansa.jwt.issuer", "ProjectSansa");
    this.audience = System.getProperty("psansa.jwt.audience", "ProjectSansaClients");
  }

  @Override
  public void filter(ContainerRequestContext ctx) {
    String authz = ctx.getHeaderString("Authorization");
    if (authz == null || !authz.startsWith("Bearer ")) {
      ctx.abortWith(jakarta.ws.rs.core.Response.status(401).build());
      return;
    }
    String token = authz.substring("Bearer ".length()).trim();
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .requireIssuer(issuer)
          .requireAudience(audience)
          .build()
          .parseClaimsJws(token)
          .getBody();

      final String uid = String.valueOf(claims.get("uid"));
      final SecurityContext orig = ctx.getSecurityContext();
      ctx.setSecurityContext(new SecurityContext() {
        @Override public Principal getUserPrincipal() { return () -> uid; }
        @Override public boolean isUserInRole(String role) { return false; }
        @Override public boolean isSecure() { return orig != null && orig.isSecure(); }
        @Override public String getAuthenticationScheme() { return "Bearer"; }
      });
    } catch (Exception e) {
      ctx.abortWith(jakarta.ws.rs.core.Response.status(401).build());
    }
  }
}

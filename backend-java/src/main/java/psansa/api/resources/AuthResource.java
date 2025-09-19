package psansa.api.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import psansa.api.security.JwtService;

import java.util.Map;
import java.util.UUID;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

  @Inject JwtService jwt;

  @POST
  @Path("/register")
  public Response register(Map<String, String> body) {
    // 最小実装：ユーザー永続化は行わず、そのままトークンを返す
    String username = body != null ? body.getOrDefault("username", "user") : "user";
    String userId = UUID.nameUUIDFromBytes(("uid:" + username).getBytes()).toString();
    String token = jwt.issue(userId, username);
    return Response.ok(Map.of("access_token", token)).build(); // 200
  }

  @POST
  @Path("/login")
  public Response login(Map<String, String> body) {
    String username = body != null ? body.getOrDefault("username", "user") : "user";
    String userId = UUID.nameUUIDFromBytes(("uid:" + username).getBytes()).toString();
    String token = jwt.issue(userId, username);
    return Response.ok(Map.of("access_token", token)).build(); // 200
  }
}

package psansa.api.resources;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import psansa.api.dto.PostReq;
import psansa.api.repo.PostRepo;
import psansa.api.security.JwtSecured;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@JwtSecured  // ← このリソース全体にJWT必須をかける
public class PostResource {

  @Inject PostRepo posts;
  @Context SecurityContext sec;

  @POST
  public Response create(PostReq req){
    // JwtAuthFilter が SecurityContext に uid を入れる
    String author = (sec != null && sec.getUserPrincipal() != null)
        ? sec.getUserPrincipal().getName()
        : null;
    if (author == null) return Response.status(401).build();

    var id = posts.insert(author, req.text(), req.lang() != null ? req.lang() : "ja");
    return Response.ok(Map.of("post_id", id.toString())).build();
  }

  @GET
  @Path("") // 既存の latest() はそのまま。JWT 要求だが閲覧だけなら外したければ @PermitAll などに変更可
  public List<Map<String, Object>> latest(@QueryParam("limit") @DefaultValue("50") int limit){
    limit = Math.min(Math.max(limit, 1), 200);
    List<Row> rows = posts.latestToday(limit);

    return rows.stream().map(r -> {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("post_id", r.getUuid("post_id").toString());
      m.put("author_id", r.getString("author_id"));
      m.put("text", r.getString("text"));
      UUID ts = r.getUuid("created_at");
      Instant created = Instant.ofEpochMilli(Uuids.unixTimestamp(ts));
      m.put("created_at", created);
      m.put("lang", r.getString("lang"));
      return m;
    }).collect(Collectors.toList());
  }
}

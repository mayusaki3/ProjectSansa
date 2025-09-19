package psansa.api.resources;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.uuid.Uuids;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import psansa.api.dto.PostReq;
import psansa.api.repo.PostRepo;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostResource {

  @Inject PostRepo posts;

  @POST
  public Response create(PostReq req, @HeaderParam("Authorization") String authz){
    if (authz == null || authz.isBlank()) return Response.status(401).build();
    String author = "extracted-userid"; // TODO: JWT 検証で uid を取り出す
    var id = posts.insert(author, req.text(), req.lang() != null ? req.lang() : "ja");
    return Response.ok(Map.of("post_id", id.toString())).build();
  }

  @GET
  public List<Map<String, Object>> latest(@QueryParam("limit") @DefaultValue("50") int limit){
    limit = Math.min(Math.max(limit, 1), 200);
    List<Row> rows = posts.latestToday(limit);

    return rows.stream().map(r -> {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("post_id", r.getUuid("post_id").toString());
      m.put("author_id", r.getString("author_id"));
      m.put("text", r.getString("text"));

      // timeuuid -> Instant
      UUID ts = r.getUuid("created_at");
      Instant created = Instant.ofEpochMilli(Uuids.unixTimestamp(ts));
      m.put("created_at", created);

      m.put("lang", r.getString("lang"));
      return m;
    }).collect(Collectors.toList());
  }
}

package psansa.api.web;

import com.datastax.oss.driver.api.core.cql.Row;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import psansa.api.dto.PostReq;
import psansa.api.repo.PostRepo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostResource {
    @Inject PostRepo posts;

    @POST
    public Response create(PostReq req, @HeaderParam("Authorization") String authz){
        // MVP-0: Authorization ヘッダの有無だけざっくり確認（厳密な検証は後続）
        if(authz==null || authz.isBlank()) return Response.status(401).build();
        String author = "extracted-userid"; // TODO: JWT 検証 & クレームから uid 抽出
        var id = posts.insert(author, req.text(), req.lang()!=null?req.lang():"ja");
        return Response.ok(Map.of("post_id", id.toString())).build();
    }

    @GET
    public List<Map<String, Object>> latest(@QueryParam("limit") @DefaultValue("50") int limit){
        limit = Math.min(Math.max(limit, 1), 200);
        List<Row> rows = posts.latestToday(limit);
        return rows.stream().map(r -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("post_id", r.getUuid("post_id").toString());
            m.put("author_id", r.getString("author_id"));
            m.put("text", r.getString("text"));
            // Instant のまま入れると Jackson が ISO-8601 で出力します
            m.put("created_at", r.getInstant("created_at"));
            m.put("lang", r.getString("lang"));
            return m;
        }).collect(java.util.stream.Collectors.toList());
    }
}

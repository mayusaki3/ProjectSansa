package psansa.api.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import psansa.api.dto.LoginReq;
import psansa.api.dto.RegisterReq;
import psansa.api.repo.UserRepo;
import psansa.api.security.JwtService;
import psansa.api.security.PasswordService;

import java.util.Map;
import java.util.UUID;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject UserRepo users;
    @Inject PasswordService pw;
    @Inject JwtService jwt;

    @POST @Path("/register")
    public Response register(RegisterReq req){
        if(req==null || req.username()==null || req.password()==null) return Response.status(400).build();
        var exists = users.findByUsername(req.username());
        if(exists.isPresent()) return Response.status(409).entity(Map.of("error","username exists")).build();
        String uid = "u_"+UUID.randomUUID();
        users.insert(uid, req.username(), pw.hash(req.password()), req.displayName()!=null?req.displayName():req.username());
        String token = jwt.issue(uid, req.username());
        return Response.ok(Map.of("access_token", token, "user", Map.of("id", uid, "name", req.username()))).build();
    }

    @POST @Path("/login")
    public Response login(LoginReq req){
        var row = users.findByUsername(req.username()).orElse(null);
        if(row==null) return Response.status(401).build();
        String uid = row.getString("user_id");
        String hash = row.getString("password_hash");
        if(!pw.verify(req.password(), hash)) return Response.status(401).build();
        String token = jwt.issue(uid, req.username());
        return Response.ok(Map.of("access_token", token, "user", Map.of("id", uid, "name", req.username()))).build();
    }
}

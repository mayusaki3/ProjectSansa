package psansa.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@ExtendWith(CassandraResource.class)
public class AuthAndPostApiIT {
  static String token;

  @BeforeAll
  static void setup(){
    // register（初回200、2回目以降409）
    String maybe =
      given().contentType("application/json")
        .body("{\"username\":\"u1\",\"password\":\"p1\"}")
      .when().post("/auth/register")
      .then().statusCode(anyOf(is(200), is(409)))
        .extract().path("access_token");

    if (maybe != null) token = maybe;
    if (token == null) {
      token =
        given().contentType("application/json")
          .body("{\"username\":\"u1\",\"password\":\"p1\"}")
        .when().post("/auth/login")
        .then().statusCode(200)
          .extract().path("access_token");
    }
  }

  @Test
  void postAndList(){
    given().contentType("application/json").header("Authorization","Bearer "+token)
      .body("{\"text\":\"hello\"}")
    .when().post("/posts")
    .then().statusCode(200).body("post_id", notNullValue());

    when().get("/posts?limit=10")
      .then().statusCode(200).body("size()", greaterThanOrEqualTo(1));
  }
}

package eu.opertusmundi.api_auth.auth_subrequest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.apache.http.HttpStatus;

@QuarkusTest
public class GreetingResourceTests {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

}
package org.Learn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.Learn.Utils.TokenManager;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTests extends BaseTest {

    @Test(priority = 1)
    public void generateToken_shouldSucceed() {
        String token = TokenManager.getToken();
        System.out.println("✅ Token: " + token);
        assert token != null && !token.isEmpty();
    }

    @Test(priority = 2)
    public void authorizedUser_shouldReturnTrue() {
        ObjectMapper mapper = new ObjectMapper();

        String token = TokenManager.getToken();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userName", config.getProperty("username"));
        requestBody.put("password", config.getProperty("password"));
        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Response response = given()
                .header("Content-Type", "application/json")
                .header("accept","application/json")
                .body(jsonBody)
                .when()
                .post("/Account/v1/Authorized")
                .then()
                .assertThat()
                .statusCode(200)
                .body(equalTo("true"))
                .extract().response();

        System.out.println("✅ Authorized Response: " + response.asString());
    }
}

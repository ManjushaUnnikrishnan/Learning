package org.Learn.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;


public class TokenManager {

    private static final ThreadLocal<String> threadToken = new ThreadLocal<>();
    private static final ThreadLocal<Instant> threadExpiry = new ThreadLocal<>();
    protected static ConfigManager config = ConfigManager.getInstance();
    private static final ObjectMapper mapper = new ObjectMapper();


    public static String getToken()
    {
        String token = threadToken.get();
        Instant expiry = threadExpiry.get();

        if(isExpired(expiry) || token == null) return generateToken(config.getProperty("username"),config.getProperty("password"));
        else return token;
    }

    private static String generateToken(String userName, String password) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userName", userName);
        requestBody.put("password", password);
        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Response response = given()
                .baseUri(config.getProperty("base_url"))
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .when()
                .post("/Account/v1/GenerateToken")
                .then()
                .statusCode(200)
                .extract().response();

        String token = response.jsonPath().getString("token");
        String expiresStr = response.jsonPath().getString("expires");

        if (token == null || token.isEmpty() || expiresStr == null || expiresStr.isEmpty()) {
            throw new RuntimeException("❌ Failed to generate token: " + response.asString());
        }

        try {
            threadToken.set(token);
            Instant expiry = Instant.parse(expiresStr);
            threadExpiry.set(expiry);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("❌ Invalid expiry format in response: " + expiresStr);
        }

        System.out.println("🔐 Token generated for thread: " + Thread.currentThread().getName() +
                " | Expires at: " + expiresStr);

        return token;

    }

    private static boolean isExpired(Instant expiry) {
        if(expiry==null) return true;
        Instant now = Instant.now();
        return now.isAfter(expiry.minusSeconds(60));
    }

    public static void clearToken() {
        threadToken.remove();
        threadExpiry.remove();
    }

}

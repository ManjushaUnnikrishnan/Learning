package org.Learn;

import io.restassured.response.Response;
import org.Learn.Utils.TokenManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class BooksTests extends BaseTest{

    @Test
    public void testGetBooks(){
        Response response = given()
                .header("accept","application/json")
                .header("Authorization", TokenManager.getToken())
                .when()
                .get("/BookStore/v1/Books")
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertNotNull(response.getBody());
        response.getBody().prettyPrint();

    }
}

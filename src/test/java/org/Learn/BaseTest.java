package org.Learn;

import io.restassured.RestAssured;
import org.Learn.Utils.ConfigManager;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    public static final ConfigManager config = ConfigManager.getInstance();

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = config.getProperty("base_url");
        System.out.println("🚀 Base URI: " + RestAssured.baseURI);
    }
}


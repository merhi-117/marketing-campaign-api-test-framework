package com.alaamerhi.marketing.base;

import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public abstract class BaseApiTest {

    protected RequestSpecification requestSpec;

    @BeforeEach
    void setUpApiTest() {

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost:3000")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        given()
            .spec(requestSpec)
        .when()
            .post("/test/reset")
        .then()
            .statusCode(204);
    }

    protected Response createCampaign(String name, String channel) {

        String requestBody = """
                {
                  "name": "%s",
                  "channel": "%s"
                }
                """.formatted(name, channel);

        return given()
                .spec(requestSpec)
                .body(requestBody)
            .when()
                .post("/campaigns");
    }
}
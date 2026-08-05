package com.alaamerhi.marketing.tests;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

class CampaignCreationTest {

    @Test
    void shouldCreateCampaignWhenRequestIsValid() {
        String campaignName =
                "Automated Campaign " + System.currentTimeMillis();

        String requestBody = """
                {
                  "name": "%s",
                  "channel": "EMAIL"
                }
                """.formatted(campaignName);

        given()
            .baseUri("http://localhost:3000")
            .contentType(ContentType.JSON)
            .body(requestBody)
            .log().all()
        .when()
            .post("/campaigns")
        .then()
            .log().all()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", greaterThan(0))
            .body("name", equalTo(campaignName))
            .body("channel", equalTo("EMAIL"))
            .body("status", equalTo("DRAFT"));
    }
}
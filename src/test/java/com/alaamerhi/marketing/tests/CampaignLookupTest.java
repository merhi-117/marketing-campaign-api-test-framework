package com.alaamerhi.marketing.tests;

import io.restassured.response.Response;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CampaignLookupTest {

    private static final String BASE_URI = "http://localhost:3000";

    @BeforeEach
    void resetTestData() {
        given()
            .baseUri(BASE_URI)
        .when()
            .post("/test/reset")
        .then()
            .statusCode(204);
    }

    private Response createCampaign(String name, String channel) {
        String requestBody = """
                {
                  "name": "%s",
                  "channel": "%s"
                }
                """.formatted(name, channel);

        return given()
            .baseUri(BASE_URI)
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/campaigns");
    }

   @Test
    void shouldReturnCampaignWhenIdExists() {
        int campaignId =
            createCampaign("Lookup Campaign", "EMAIL")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
            .baseUri(BASE_URI)
            .pathParam("id", campaignId)
        .when()
            .get("/campaigns/{id}")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", equalTo(campaignId))
            .body("name", equalTo("Lookup Campaign"))
            .body("channel", equalTo("EMAIL"))
            .body("status", equalTo("DRAFT"));
    }

    @Test
    void shouldReturnNotFoundWhenCampaignDoesNotExist() {
        given()
            .baseUri(BASE_URI)
            .pathParam("id", 999)
        .when()
            .get("/campaigns/{id}")
        .then()
            .log().all()
            .statusCode(404)
            .body("errorCode", equalTo("CAMPAIGN_NOT_FOUND"))
            .body("message", equalTo("Campaign with ID 999 was not found"));
    }

    @Test
    void shouldRejectCampaignIdWhenItIsNotANumber() {
        given()
            .baseUri(BASE_URI)
            .pathParam("id", "abc")
        .when()
            .get("/campaigns/{id}")
        .then()
            .log().all()
            .statusCode(400)
            .body("errorCode", equalTo("INVALID_CAMPAIGN_ID"))
            .body(
                "message",
                equalTo("Campaign ID must be a positive integer")
            );
    }
}
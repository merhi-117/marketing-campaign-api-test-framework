package com.alaamerhi.marketing.tests;

import com.alaamerhi.marketing.base.BaseApiTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CampaignLookupTest extends BaseApiTest {

   @Test
    void shouldReturnCampaignWhenIdExists() {
        int campaignId =
            createCampaign("Lookup Campaign", "EMAIL")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
            .spec(requestSpec)
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
            .spec(requestSpec)
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
            .spec(requestSpec)
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
package com.alaamerhi.marketing.tests;

import com.alaamerhi.marketing.base.BaseApiTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

class CampaignListTest extends BaseApiTest {

    @Test
    void shouldReturnEmptyListWhenNoCampaignsExist() {

        given()
            .spec(requestSpec)
        .when()
            .get("/campaigns")
        .then()
            .statusCode(200)
            .body("", hasSize(0));
    }

    @Test
    void shouldReturnAllCampaigns() {

        createCampaign("Email Campaign", "EMAIL")
            .then()
            .statusCode(201);

        createCampaign("SMS Campaign", "SMS")
            .then()
            .statusCode(201);

        given()
            .spec(requestSpec)
        .when()
            .get("/campaigns")
        .then()
            .statusCode(200)
            .body("", hasSize(2))
            .body(
                "name",
                hasItems(
                    "Email Campaign",
                    "SMS Campaign"
                )
            );
    }
}
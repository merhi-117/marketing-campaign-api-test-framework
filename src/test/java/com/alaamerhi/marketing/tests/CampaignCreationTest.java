package com.alaamerhi.marketing.tests;

import io.restassured.response.Response;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

class CampaignCreationTest {

    private static final String BASE_URI = "http://localhost:3000";

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
    void shouldCreateCampaignWhenRequestIsValid() {
        String campaignName = "Automated Campaign";

        createCampaign(campaignName, "EMAIL")
            .then()
            .log().all()    
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", greaterThan(0))
            .body("name", equalTo(campaignName))
            .body("channel", equalTo("EMAIL"))
            .body("status", equalTo("DRAFT"));
    }

    @Test
    void shouldRejectCampaignWhenNameIsMissing() {
        String requestBody = """
                {
                "channel": "EMAIL"
                }
                """;

        given()
            .baseUri(BASE_URI)
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/campaigns")
        .then()
            .log().all()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("errorCode", equalTo("VALIDATION_ERROR"))
            .body("message", equalTo("Campaign name is required"));
    }

    @Test
    void shouldRejectCampaignWhenNameIsBlank() {
        createCampaign("  ", "EMAIL")
            .then()
            .log().all()
            .statusCode(400)
            .body("errorCode", equalTo("VALIDATION_ERROR"))
            .body("message", equalTo("Campaign name cannot be blank"));
    }

    @Test
    void shouldRejectCampaignWhenChannelIsInvalid() {
        createCampaign("InvalidCahnnel", "WHATSAPP")
            .then()
            .log().all()
            .statusCode(400)
            .body("errorCode", equalTo("INVALID_CHANNEL"))
            .body("message", equalTo("Channel must be EMAIL or SMS"));
    }

    @Test
    void shouldRejectDuplicateCampaignName() {
        createCampaign("Duplicate Campaign", "EMAIL")
            .then()
            .statusCode(201);
        
        createCampaign("DupLIcate CamPaign", "SMS")
            .then()
            .log().all()
            .statusCode(409)
            .body("errorCode", equalTo("DUPLICATE_CAMPAIGN"))
            .body("message", equalTo("A campaign with this name already exists"));
    }



    @BeforeEach
    void resetTestData() {
        given()
        .baseUri(BASE_URI)
        .when()
        .post("/test/reset")
        .then()
        .statusCode(204);
    }
}
package com.alaamerhi.marketing.tests;

import com.alaamerhi.marketing.base.BaseApiTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CampaignStatusTest extends BaseApiTest {

    private Response updateStatus(
            int campaignId,
            String newStatus) {

        String requestBody = """
                {
                  "status": "%s"
                }
                """.formatted(newStatus);

        return given()
            .spec(requestSpec)
            .pathParam("id", campaignId)
            .body(requestBody)
        .when()
            .patch("/campaigns/{id}/status");
    }

    private int extractCampaignId(
            String name,
            String channel) {

        return createCampaign(name, channel)
            .then()
            .statusCode(201)
            .extract()
            .path("id");
    }

    @Test
    void shouldActivateDraftCampaign() {

        int campaignId =
            extractCampaignId(
                "Draft Campaign",
                "EMAIL"
            );

        updateStatus(campaignId, "ACTIVE")
            .then()
            .statusCode(200)
            .body("status", equalTo("ACTIVE"));
    }

    @Test
    void shouldPauseActiveCampaign() {

        int campaignId =
            extractCampaignId(
                "Active Campaign",
                "EMAIL"
            );

        updateStatus(campaignId, "ACTIVE")
            .then()
            .statusCode(200)
            .body("status", equalTo("ACTIVE"));

        updateStatus(campaignId, "PAUSED")
            .then()
            .statusCode(200)
            .body("status", equalTo("PAUSED"));
    }

    @Test
    void shouldRejectUnsupportedStatus() {

        int campaignId =
            extractCampaignId(
                "Unsupported Status Campaign",
                "EMAIL"
            );

        updateStatus(campaignId, "UNKNOWN")
            .then()
            .statusCode(400)
            .body(
                "errorCode",
                equalTo("INVALID_STATUS")
            );
    }

    @Test
    void shouldRejectInvalidStatusTransition() {

        int campaignId =
            extractCampaignId(
                "Transition Campaign",
                "EMAIL"
            );

        updateStatus(campaignId, "COMPLETED")
            .then()
            .statusCode(409)
            .body(
                "errorCode",
                equalTo("INVALID_STATUS_TRANSITION")
            );
    }

    @Test
    void shouldNotReactivateCompletedCampaign() {

        int campaignId =
            extractCampaignId(
                "Completed Campaign",
                "EMAIL"
            );

        updateStatus(campaignId, "ACTIVE")
            .then()
            .statusCode(200);

        updateStatus(campaignId, "COMPLETED")
            .then()
            .statusCode(200);

        updateStatus(campaignId, "ACTIVE")
            .then()
            .statusCode(409)
            .body(
                "errorCode",
                equalTo("INVALID_STATUS_TRANSITION")
            );
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownCampaign() {

        int unknownCampaignId = 999;

        updateStatus(unknownCampaignId, "ACTIVE")
            .then()
            .statusCode(404)
            .body(
                "errorCode",
                equalTo("CAMPAIGN_NOT_FOUND")
            )
            .body(
                "message",
                equalTo(
                    "Campaign with ID 999 was not found"
                )
            );
    }
}
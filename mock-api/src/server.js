const express = require("express");

const app = express();
const PORT = 3000;

// In-memory storage for campaigns. In a real application, this would be replaced with a database.
const campaigns = [];
let nextCampaignId = 1;

const allowedStatuses = ["DRAFT", "ACTIVE", "PAUSED", "COMPLETED"];

const AllowedTransitions = {
    "DRAFT": ["ACTIVE"],
    "ACTIVE": ["PAUSED", "COMPLETED"],
    "PAUSED": ["ACTIVE", "COMPLETED"],
    "COMPLETED": []
}

// Allow Express to read JSON request bodies.
app.use(express.json());

// Simple endpoint used to check whether the API is running.
app.get("/health", (request, response) => {
    response.status(200).json({
        status: "UP"
    });
});

// Endpoint to create a new campaign. The request body should contain the campaign name and channel.
app.post("/campaigns", (request, response) => {

    // Extract the name and channel from the request body.
    const { name, channel } = request.body ?? {};

    if (name === undefined || name === null) {
        return response.status(400).json({
            errorCode: "VALIDATION_ERROR",
            message: "Campaign name is required"
        });
    }

    if (typeof name !== "string") {
        return response.status(400).json({
            errorCode: "VALIDATION_ERROR",
            message: "Campaign name must be a string"
        });
    }

    const normalizedName = name.trim();

    if (normalizedName.length === 0) {
        return response.status(400).json({
            errorCode: "VALIDATION_ERROR",
            message: "Campaign name cannot be blank"
        });
    }

    if (normalizedName.length > 100) {
        return response.status(400).json({
            errorCode: "VALIDATION_ERROR",
            message: "Campaign name cannot exceed 100 characters"
        });
    }

    const duplicateCampaign = campaigns.some((campaign) => {
        return campaign.name.toLowerCase() === normalizedName.toLowerCase();
    });

    if (duplicateCampaign) {
        return response.status(409).json({
            errorCode: "DUPLICATE_CAMPAIGN",
            message: "A campaign with this name already exists"
        });
    }


    if (channel === undefined || channel === null) {
        return response.status(400).json({
            errorCode: "VALIDATION_ERROR",
            message: "Campaign channel is required"
        });
    }

    const allowedChannels = ["EMAIL", "SMS"];

    if (typeof channel !== "string" || !allowedChannels.includes(channel)) {
        return response.status(400).json({
            errorCode: "INVALID_CHANNEL",
            message: "Channel must be EMAIL or SMS"
        });
    }


    // create a new campaign object with the provided name and channel, and set its status to "DRAFT".
    const campaign = {
        id: nextCampaignId,
        name: name,
        channel: channel,
        status: "DRAFT"
    };

    // Add the new campaign to the in-memory storage and increment the next campaign ID.
    campaigns.push(campaign);
    nextCampaignId++;

    return response.status(201).json(campaign);
});

// Endpoint to retrieve a campaign by its ID. The ID is provided as a path parameter.
app.get("/campaigns/:id", (request, response) => {
    const campaignId = Number(request.params.id);

    // Validate that the campaign ID is a positive integer. If not, return a 400 Bad Request response with an appropriate error message.
    if (!Number.isInteger(campaignId) || campaignId <= 0) {
        return response.status(400).json({
            errorCode: "INVALID_CAMPAIGN_ID",
            message: "Campaign ID must be a positive integer"
        });
    }

    // Find the campaign with the specified ID in the in-memory storage.
    const campaign = campaigns.find(item => item.id === campaignId);

    // If the campaign is not found, return a 404 Not Found response with an appropriate error message.
    if (!campaign) {
        return response.status(404).json({
            errorCode: "CAMPAIGN_NOT_FOUND",
            message: `Campaign with ID ${campaignId} was not found`
        });
    }

    return response.status(200).json(campaign);
});

// Endpoint to reset the in-memory storage and next campaign ID. 
app.post("/test/reset", (request, response) => {
    // Reset the in-memory storage and next campaign ID.
    campaigns.length = 0;
    nextCampaignId = 1;

    return response.status(204).send();
});

// retrieve all campaigns
app.get("/campaigns", (request, response) => {
    return response.status(200).json(campaigns);
});

app.patch("/campaigns/:id/status", (request, response) => {
      
    const { status: newStatus } = request.body ?? {};
    const campaignId = Number(request.params.id);

    if (!Number.isInteger(campaignId) || campaignId <= 0) {
        return response.status(400).json({
            errorCode: "INVALID_CAMPAIGN_ID",
            message: "Campaign ID must be a positive integer"
        });
    }

    const campaign = campaigns.find((item) => item.id === campaignId);

    if (!campaign) {
        return response.status(404).json({
            errorCode: "CAMPAIGN_NOT_FOUND",
            message: `Campaign with ID ${campaignId} was not found`
        });
    }

    if (newStatus === undefined || newStatus === null || newStatus.trim() === "") {
            return response.status(400).json({
                errorCode: "VALIDATION_ERROR",
                message: "New status is required"
            });
    }

    if (typeof newStatus !== "string" || !allowedStatuses.includes(newStatus)) {
        return response.status(400).json({
            errorCode: "INVALID_STATUS",
            message: `Status must be one of ${allowedStatuses.join(", ")}`
        });
    }

    const currentStatus = campaign.status;

    if (!AllowedTransitions[currentStatus].includes(newStatus)) {
        return response.status(409).json({
            errorCode: "INVALID_STATUS_TRANSITION",
            message: `Cannot transition from ${currentStatus} to ${newStatus}`
        });
    }

    campaign.status = newStatus;

    return response.status(200).json(campaign);

});

app.listen(PORT, () => {
    console.log(`Marketing Campaign API running on http://localhost:${PORT}`);
});
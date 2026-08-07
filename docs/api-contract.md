#   marketing Campaign API Contract

## 1. Purpose
   
   This document defines the initial contract for a local Marketing Campaign API. The API supports creating and managing campaigns and subscribing eligible customers to campaigns.

## 2. Endpoint Summary   

| Method  |                  Endpoint                 |         Purpose               |
|---------|-------------------------------------------|-------------------------------|
|  POST   | /campaigns                                | Create a new campaign         |
|  GET    | /campaigns/{id}                           | Retrieve a campaign           |
|  GET    | /campaigns                                | Retrieve all campaigns        |
|  PATCH  | /campaigns/{id}/status                    | Change campaign status        |
|  POST   | /campaigns/{id}/subscribers               | Subscribe a customer          |
|  GET    | /campaigns/{id}/subscribers               | Retrieve campaign subscribers |
|  DELETE | /campaigns/{id}/subscribers/{customerId}  | Remove a subscriber           |
|  POST   | /test/reset                               | Reset local test data         |


## 3. Create a Campaign — `POST /campaigns`
   Creates a new marketing campaign.

### Request

   POST /campaigns
   Content-Type: application/json

  ```json
   {
   "name": "Summer Credit Card Campaign",
   "channel": "EMAIL"
   }
  ```
### Request Fields

|   Field    |    Type   |  Required |  Discription                                   |
|------------|-----------|-----------|------------------------------------------------|
|  name      |  String   |    Yes    |  Unique name used to identify the campaign     |
|  channel   |  String   |    Yes    |  Communication channel usde by the campaign    |

### Validation Rules

   - name is required.
   - name cannot be empty or contain whitespace.
   - name cannot exceed 100 characters.
   - Campaign names must be unique
   - Campaign-name uniqueness is checked case-insensitively after trimming surrounding whitespace.
   - channel is required.
   - channel must be either EMAIL or SMS.
   - channel values are case-sensitive.
   - The server assignes every newly created campaign the status DRAFT.
   - The server generates the campaign ID.
   
### Successful Response
   201 Created
   Content-Type: application/json

   ```json
      {
      "id": 1,
      "name": "Summer Credit Card Campaign",
      "channel": "EMAIL",
      "status": "DRAFT"
      }
   ```
### Error Responses

### Missing Campaign Name

 -  400 bad Request
   Content-Type: application/json

   ```json
      {
         "errorCode": "VALIDATION_ERROR",
         "message": "Campaign name is required"
      }
   ```

### Blank Campaign Name

 - 400 Bad Request
   Content-Type: application/json

   ```json
      {
         "errorCode": "VALIDATION_ERROR",
         "message": "Campaign name cannot be blank"
      }
   ```
### Invalid Channel
 - 400 Bad Request
   Content-Type: application/json

```json
   {
      "errorCode": "INVALID_CHANNEL",
      "message": "Channel must be EMAIL or SMS"
   }
```

### Duplicate Campaign Name

 - 409 conflict
    Content-Type: application/json
    
   ```json
      {
         "errorCode": "DUPLICATE_CAMPAIGN",
         "message": "A Campaign with this name already exists"
      }
   ```


## Reset Test Data - `POST /test/reset`

Clears all campaigns stored by local test API and resets the next campaign ID to 1

This endpoint exists only for local testing and must not be exposed in a production environment.

### Request

```http
POST /test/reset
```

## Successful Response

204 no content

## Why use 204 no content?

The request succeeds but the client does not need any data back.

The important result is the server-side state change:

```text
campaigns become empty
nextCampaignnId becomes 1

## Retrieve All Campaigns — `GET /campaigns`

Return all campaigns currently stored by the API.

### Request

```http
GET/campaigns

## Successful Response

200 OK
content-Type: application/json

[
  {
    "id": 1,
    "name": "Summer Campaign",
    "channel": "EMAIL",
    "status": "DRAFT"
  },
  {
    "id": 2,
    "name": "SMS Promotion",
    "channel": "SMS",
    "status": "DRAFT"
  }
]

if no campaigns exist, the API returns:

[]

not

404

Since the collection exists, but contains zero records.


## Change Campaign Status — `PATCH /campaigns/{id}/status`

Updates the lifecycle status of an existing campaign.

### Request

```http
PATCH /campaigns/1/status
Content-Type: application/json

{
  "status": "ACTIVE"
}
```
### Allowed Status Values

- DRAFT
- ACTIVE
- PAUSED
- COMPLETED

### Transition Rules

|  Current Status | Allowed Next Status|
|-----------------|--------------------|
|     DRAFT       |        ACTIVE      |
|     ACTIVE      |  PAUSED, COMPLETED |
|     PAUSED      |  ACTIVE, COMPLETED |
|    COMPLETED    |        None        |

Requesting the campaign's current status is allowed and does not change the resource.

## Successful Response

200 ok

{
  "id": 1,
  "name": "Summer Campaign",
  "channel": "EMAIL",
  "status": "ACTIVE"
}

## Errors

- 400 — Invalid campaigns ID
- 400 — Missing status
- 400 — Unsupported status value
- 404 — Campaign does not exist
- 409 — Request transition is not allowed
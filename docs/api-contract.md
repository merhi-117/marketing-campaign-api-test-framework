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
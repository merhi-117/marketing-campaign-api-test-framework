# Marketing Campaign API Test Framework

A Java API automation portfolio project built to practice **Quality Engineering, REST API testing, reusable automation framework design, and Git/GitHub workflow**.

The project tests a local marketing-campaign REST API using **Java 21, REST Assured, JUnit, and Maven**. A lightweight **Node.js/Express** service acts as the system under test so API behavior, validation rules, error handling, and lifecycle transitions can be controlled and tested end to end.

## What This Project Demonstrates

- API test design and execution
- Positive and negative testing
- REST Assured automation with Java
- JUnit test organization and isolation
- Reusable REST Assured request specifications
- HTTP and JSON response validation
- Business-rule and state-transition testing
- Deterministic test-data reset
- Root-cause analysis and automation debugging
- Git feature branches, pull requests, review, and merge workflow

## Current Test Coverage

The automated suite currently contains **16 passing integration/API tests**.

|       Area        |                           Scenarios                                       |
|-------------------|---------------------------------------------------------------------------|
| Campaign Creation | valid creation, missing name, blank name, invalid channel, duplicate name |
| Campaign Lookup   | existing campaign, unknown campaign, invalid campaign ID                  |
| Campaign Listing  | empty campaign list, multiple campaigns                                   |
| Campaign Status   | DRAFT -> ACTIVE, ACTIVE -> PAUSED, unsupported status, invalid transition, completed campaign cannot reactivate, unknown campaign |

Latest verified local result:

```text
Tests run: 16
Failures: 0
Errors: 0
BUILD SUCCESS
```

## API Endpoints

| Method |         Endpoint         |                 Purpose                       |
|--------|--------------------------|-----------------------------------------------|
| `GET`  | `/health`                | Verify the local API is running               |
| `POST` | `/campaigns`             | Create a campaign                             |
| `GET`  | `/campaigns/{id}`        | Retrieve a campaign by ID                     |
| `GET`  | `/campaigns`             | Retrieve all campaigns                        |
| `PATCH`| `/campaigns/{id}/status` | Update campaign lifecycle status              |
| `POST` | `/test/reset`            | Reset local test data for deterministic tests |

Subscriber endpoints are part of the planned next phase.

## Campaign Business Rules

New campaigns are created with status:

```text
DRAFT
```

Supported lifecycle states:

```text
DRAFT -> ACTIVE
ACTIVE -> PAUSED
ACTIVE -> COMPLETED
PAUSED -> ACTIVE
PAUSED -> COMPLETED
COMPLETED -> final state
```

Examples of validated API behavior:

- `201 Created` for successful campaign creation
- `204 No Content` for test-data reset
- `400 Bad Request` for invalid input or unsupported status values
- `404 Not Found` for missing campaign resources
- `409 Conflict` for duplicate campaign names or invalid lifecycle transitions

## Test Framework Design

Shared test behavior is centralized in `BaseApiTest`.

```text
BaseApiTest
├── reusable RequestSpecification
├── base URI and JSON configuration
├── @BeforeEach test-data reset
└── reusable campaign creation helper

CampaignCreationTest
CampaignLookupTest
CampaignListTest
CampaignStatusTest
```

`@BeforeEach` calls `/test/reset` so every test begins with a known state. This prevents tests from depending on execution order or data created by another test.

## Project Structure

```text
marketing-campaign-api-test-framework/
├── docs/
│   └── api-contract.md
├── mock-api/
│   ├── src/
│   │   └── server.js
│   ├── package.json
│   └── package-lock.json
├── src/
│   └── test/
│       └── java/
│           └── com/
│               └── alaamerhi/
│                   └── marketing/
│                       ├── base/
│                       │   └── BaseApiTest.java
│                       └── tests/
│                           ├── CampaignCreationTest.java
│                           ├── CampaignLookupTest.java
│                           ├── CampaignListTest.java
│                           └── CampaignStatusTest.java
├── LEARNING_NOTES.md
├── pom.xml
└── README.md
```

## Technologies

- Java 21
- REST Assured
- JUnit
- Maven
- Node.js
- Express
- JSON / HTTP
- Git / GitHub
- PowerShell for manual API checks

## Run the Project

### Prerequisites

Install:

- Java 21+
- Maven 3.9+
- Node.js and npm
- Git

### 1. Install the local API dependencies

```bash
cd mock-api
npm install
```

### 2. Start the API

```bash
npm start
```

The API runs at:

```text
http://localhost:3000
```

### 3. Run the automated tests

Open another terminal from the repository root:

```bash
mvn test
```

A successful run ends with:

```text
BUILD SUCCESS
```

## Example REST Assured Test Flow

```java
given()
    .spec(requestSpec)
    .pathParam("id", campaignId)
    .body(requestBody)
.when()
    .patch("/campaigns/{id}/status")
.then()
    .statusCode(200)
    .body("status", equalTo("ACTIVE"));
```

The tests follow the familiar **Given / When / Then** structure:

- **Given** - configure request data and preconditions
- **When** - execute the API operation
- **Then** - validate status codes, JSON fields, and business behavior

## Debugging Lessons

During development, an automated status test incorrectly sent:

```text
PUT /campaigns/{id}/status
```

while the API contract defined:

```text
PATCH /campaigns/{id}/status
```

Manual API testing confirmed the Express endpoint worked, which isolated the defect to the test automation. The helper was then corrected to use `PATCH`.

The status helper was also refactored to return the REST Assured `Response` instead of assuming every response contained a successful `status` field. This allowed each test to make scenario-specific assertions for success and error responses.

## Git Workflow Used

Development followed a feature-branch workflow:

```text
main
  |
  └── feature/campaign-endpoints
          |
          ├── API implementation
          ├── automated tests
          ├── framework refactoring
          └── documentation
                    |
                    v
              Pull Request #1
                    |
                    v
                  main
```

The completed campaign feature was reviewed and merged into `main` through GitHub.

## Next Planned Improvements

- Subscriber and marketing-consent API scenarios
- BDD with Cucumber
- GitHub Actions for CI/CD/continuous testing
- Service virtualization with WireMock
- Accessibility testing practice
- Additional reporting and quality metrics

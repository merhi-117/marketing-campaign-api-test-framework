# Marketing Campaign API Test Framework — Learning Notes

## 1. API Contract Design

### What I built

- Created the initial API endpoint summary.
- Defined the main operations needed for campaigns and subscribers.
- Documented the initial contract for `POST /campaigns`.
- Added request fields, validation rules, successful responses, and structured error responses.

### What I expected

- I expected the contract to identify the HTTP method, endpoint, and purpose.
- I expected some details to be missing because this was my first time designing an API contract independently.

### What happened

- The endpoint summary covered the main operations.
- I learned that an endpoint list is only the beginning of an API contract.
- A complete contract must also define request data, response data, validation rules, business rules, and HTTP status codes.

### What failed

- Maven was initially not recognized because it was not installed or added to `PATH`.
- The first Markdown table divider was structured incorrectly.
- The project and contract filenames contained spelling errors.
- The first contract version did not include request bodies, response bodies, validation rules, or status codes.
- I initially included `404 Not Found` for campaign creation, although that status is more appropriate when retrieving a missing resource.

### Why it failed

- Maven is a separate build tool and is not installed automatically with Java.
- I initially focused on listing endpoints rather than documenting the complete agreement between the API client and server.
- I was still learning when to use different HTTP status codes.

### What I changed

- Corrected the project and file names.
- Corrected the endpoint table.
- Defined the contract for `POST /campaigns`.
- Corrected the campaign-creation error responses.
- Removed the incorrect `404 Not Found` response from campaign creation.
- Added request-field definitions and validation rules.
- Changed error responses to structured JSON objects containing fields such as `errorCode` and `message`.

### What I learned

- Java and Maven are separate tools.
- Maven manages dependencies and runs Java builds and tests.
- An API contract includes requests, responses, validation rules, business rules, and status codes.
- Test data can be reset, but test cases themselves are not reset.
- `404 Not Found` applies when a requested resource cannot be found.
- `409 Conflict` can represent a request that conflicts with existing data.
- Validation rules become the basis for positive and negative automated tests.

### What I would improve

- Define the complete contract for every endpoint.
- Add examples of successful and unsuccessful requests.
- Confirm that every business rule has at least one corresponding test scenario.
- Keep the API contract synchronized with the implementation.

---

## 2. Development Environment

### What I configured

- Installed Apache Maven 3.9.16.
- Configured `MAVEN_HOME`.
- Added Maven's `bin` directory to `PATH`.
- Verified that Maven uses Java 21.
- Confirmed that Node.js, npm, Git, Java, and Maven work from PowerShell.

### What I learned

- Environment variables allow development tools to be located from the command line.
- `JAVA_HOME` points to the JDK installation.
- `MAVEN_HOME` points to the Maven installation.
- `mvn -version` verifies both Maven and the Java runtime Maven is using.

---

## 3. API Server Skeleton

### What I built

- Created a Node.js project for the local mock API.
- Installed Express.
- Added a `GET /health` endpoint.
- Started the API on port `3000`.
- Sent a request to the API from PowerShell.

### What I expected

- I expected `GET /health` to return status `200`.
- I expected the response body to contain `"status": "UP"`.

### What happened

- The Express server started successfully.
- PowerShell received the expected response.
- The response included status `200 OK` and JSON content.

### What I learned

- An API client sends an HTTP request to a server endpoint.
- A route combines an HTTP method and a URL path.
- Express converts JavaScript objects into JSON responses.
- A port identifies the local process receiving the request.
- A health endpoint confirms that the service is running and available.
- `express.json()` allows the server to parse incoming JSON request bodies.

---

## 4. Campaign Creation and Lookup

### What I built

- Created an in-memory `campaigns` array.
- Implemented valid campaign creation.
- Added missing-name, blank-name, invalid-channel, and maximum-length validation.
- Deliberately sent invalid requests to verify the validation behaviour.
- Added case-insensitive duplicate-name validation.
- Implemented campaign lookup by ID.
- Tested successful lookup.
- Tested the `404 Not Found` scenario.
- Tested invalid campaign ID syntax.

### Questions and Answers

1. **Why must validation happen before modifying the array?**  
   Validation must happen first so invalid data is not stored. An invalid request should not create a campaign, consume an ID, or change the server's state.

2. **Why is a duplicate campaign a `409` rather than a `400`?**  
   The request contains valid fields, but it conflicts with data that already exists. Since campaign names must be unique, `409 Conflict` describes the problem more accurately.

3. **Why does `request.params.id` need conversion?**  
   URL path parameters are received as strings. The campaign IDs stored in memory are numbers, so the value must be converted before comparing it with stored IDs.

4. **What makes the REST Assured test an integration/API test rather than a unit test?**  
   The test sends a real HTTP request to a running Express server and validates the response produced by several parts working together: routing, JSON parsing, validation, business logic, and response generation. A unit test would normally test one isolated function or class without making a network request or requiring the full server to run.

5. **Why must the server be running before `mvn test`?**  
   The REST Assured test acts as an API client and sends requests to `http://localhost:3000`. The Express server must therefore be running and listening on that port.

6. **Why did changing the expected status from `201` to `200` cause the test to fail?**  
   The API correctly returned `201 Created`, but the test expected `200 OK`. REST Assured detected the mismatch and caused Maven's test phase to fail.

7. **What weakness does the timestamp approach have, and how will `/test/reset` improve it?**  
   A timestamp avoids duplicate names, but it creates unpredictable data and leaves old campaigns in memory while the server is running. A `/test/reset` endpoint will allow each test to begin from a known, clean state, making the tests repeatable and deterministic.

---

## 5. Maven, JUnit, and REST Assured

### What I built

- Created a Maven test project.
- Added REST Assured and JUnit dependencies.
- Created the first REST Assured campaign-creation test.
- Used `given()`, `when()`, and `then()` to prepare the request, send it, and validate the response.
- Verified the response status, content type, campaign ID, name, channel, and status.
- Deliberately changed the expected status code to make the test fail.
- Restored the correct expectation and confirmed that the build passed.

### Test results

- The first correct execution returned `201 Created` and passed.
- The deliberately incorrect expectation produced a clear assertion failure:
  - Expected: `200`
  - Actual: `201`
- After restoring the expected status to `201`, the test passed again.
- Maven reported:
  - Tests run: `1`
  - Failures: `0`
  - Errors: `0`
  - Build result: `BUILD SUCCESS`

### What I learned

- JUnit discovers and runs the test method.
- Maven Surefire runs JUnit tests during Maven's test phase.
- REST Assured sends the HTTP request and validates the response.
- `given()` prepares the request.
- `when()` performs the API operation.
- `then()` contains response assertions.
- A passing test shows that the current implementation matches the tested expectations.
- A deliberately failing test proves that the automation can detect incorrect behaviour.
- Request and response logging makes failures easier to investigate.

---

## 6. Git and GitHub Workflow

### What I did

- Created the `feature/campaign-endpoints` branch.
- Committed the campaign endpoint implementation.
- Added and committed the Maven project and first REST Assured test.
- Pushed the feature branch to GitHub.

### What I learned

- `git add .` stages changed and new files.
- `git commit` creates a local project snapshot.
- `git push` uploads local commits to GitHub.
- A feature branch keeps unfinished development separate from `main`.
- Clear commit messages make project history easier to understand.

---

---

## 9. Test Isolation and Reusable REST Assured Setup

### What I built

- Added a local `POST /test/reset` endpoint.
- Used JUnit `@BeforeEach` so every automated test starts from a clean state.
- Reset the in-memory campaign list before each test.
- Reset `nextCampaignId` so IDs are predictable.
- Removed the need to generate campaign names with timestamps.
- Created a reusable `BaseApiTest`.
- Created a reusable REST Assured `RequestSpecification`.
- Moved the common base URI, JSON content type, JSON accept header, reset logic, and campaign-creation helper into the base class.
- Refactored campaign creation and lookup test classes to inherit from `BaseApiTest`.

### What I learned

- `@BeforeEach` runs before every JUnit `@Test` method.
- Tests should not depend on execution order because JUnit does not guarantee that tests run in the same order they appear in the source file.
- Test isolation means every test begins from a predictable state and does not depend on data left by another test.
- Resetting test data makes automated tests deterministic and repeatable.
- A reusable REST Assured `RequestSpecification` prevents repeated configuration in every test.
- Refactoring should improve the structure of the test framework without changing the externally observable API behaviour.
- Inheritance can be useful in a test framework when child test classes genuinely share setup and helper behaviour.

---

## 10. Campaign Listing

### What I built

- Implemented `GET /campaigns`.
- Defined that an empty collection returns `200 OK` with `[]` rather than `404 Not Found`.
- Manually tested the endpoint with zero campaigns and multiple campaigns.
- Added automated tests for:
  - An empty campaign list.
  - Returning all created campaigns.

### What I learned

- A collection endpoint can exist successfully even when it contains no resources.
- `200 OK` with an empty array communicates that the collection exists but currently has no items.
- REST Assured can validate the size of a JSON array and values contained in objects inside the array.
- Test setup should create only the data needed by a specific test.

---

## 11. Campaign Status Lifecycle

### What I built

- Implemented `PATCH /campaigns/{id}/status`.
- Defined the supported statuses:
  - `DRAFT`
  - `ACTIVE`
  - `PAUSED`
  - `COMPLETED`
- Added lifecycle transition rules.
- Manually tested successful status changes and invalid transitions.
- Created `CampaignStatusTest` to automate status-related scenarios.

### Transition Rules

| Current Status | Allowed Next Status  |
|----------------|----------------------|
|    `DRAFT`     |      `ACTIVE`        |
|    `ACTIVE`    | `PAUSED`, `COMPLETED`|
|    `PAUSED`    | `ACTIVE`, `COMPLETED`|
|   `COMPLETED`  |         None         |

### What I learned

- A field value can be valid while a requested state transition is still invalid.
- An unsupported status such as `UNKNOWN` is a `400 Bad Request`.
- A transition such as `DRAFT -> COMPLETED` can be a `409 Conflict` because `COMPLETED` is a valid status but conflicts with the resource's current state and lifecycle rules.
- State-transition testing is an example of testing business logic rather than only checking HTTP syntax.
- A completed campaign is treated as a final state and cannot be reactivated.

---

## 12. Debugging the Status Tests

### What failed

The REST Assured campaign-status tests initially failed even though the PATCH endpoint worked manually.

The failures included:

- Expected `200`, `400`, or `409` responses but receiving `404`.
- Express reporting `Cannot PUT /campaigns/{id}/status`.
- Error-response tests trying to assert a `status` field that did not exist.

### Root Cause 1 — Wrong HTTP Method

The API contract and Express server used:

```text
PATCH /campaigns/{id}/status
```

but the Java helper used:

```java
.put("/campaigns/{id}/status")
```

`PUT` and `PATCH` are different HTTP methods and therefore different routes.

The helper needed to use:

```java
.patch("/campaigns/{id}/status")
```

### Root Cause 2 — Helper Was Making Assertions

The original reusable helper always asserted:

```java
.body("status", equalTo(newStatus))
```

This worked only for successful responses.

Error responses instead contained fields such as:

```json
{
  "errorCode": "CAMPAIGN_NOT_FOUND",
  "message": "Campaign with ID 999 was not found"
}
```

They did not contain a `status` field.

### What I changed

- Changed the status request from `PUT` to `PATCH`.
- Refactored `updateStatus()` so it performs the HTTP request and returns the REST Assured `Response`.
- Moved assertions into individual test methods.
- Successful tests assert the updated campaign `status`.
- Failure tests assert the appropriate `errorCode`, message, and HTTP status.
- The HTTP method is part of an API endpoint's contract. The same URL with different methods represents different operations.
- A reusable helper should usually perform a shared action without assuming that every response has the same structure.
- The individual test should own assertions that describe the expected behaviour for that particular scenario.
- Failure output can reveal whether the defect is in the application or in the test code itself.
- Manual testing is valuable for isolating whether an endpoint works before debugging automation around it.

---

## 13. Current Project Status

### Completed

- Initial API contract
- Local Express test API
- Health endpoint
- In-memory campaign storage
- Campaign creation
- Campaign validation
- Duplicate-name detection
- Campaign lookup by ID
- `GET /campaigns`
- `PATCH /campaigns/{id}/status`
- Campaign lifecycle transition rules
- `/test/reset`
- Manual positive and negative API testing
- Java 21 and Maven configuration
- JUnit and REST Assured tests
- Automated positive campaign-creation tests
- Automated negative campaign-creation tests
- Automated lookup tests
- Automated list tests
- Campaign status test class
- Reusable `BaseApiTest`
- Reusable REST Assured `RequestSpecification`
- Test isolation with `@BeforeEach`
- Feature-branch Git/GitHub workflow
- Debugging incorrect REST Assured HTTP methods and response assertions

### Remaining for the Core Framework

- Confirm the complete automated suite passes after the status-test fixes.
- Review Git changes before committing.
- Commit and push the final campaign feature work.
- Open a GitHub pull request.
- Review the pull request.
- Merge `feature/campaign-endpoints` into `main`.
- Synchronize local `main`.
- Improve the project README.
- Complete the remaining subscriber API contract and endpoints in a later stage.
- Add Cucumber/BDD.
- Add GitHub Actions.
- Add WireMock/service virtualization.

---

## 14. Key Framework Lessons So Far

- Design the API contract before automating it.
- Translate business rules into positive and negative test scenarios.
- Test the API manually before automating complex behaviour.
- Keep tests isolated from one another.
- Use reusable framework components only after duplication becomes clear.
- Keep common request configuration in a reusable request specification.
- Keep helpers focused on performing actions and let tests own scenario-specific assertions.
- Verify both successful and unsuccessful responses.
- Treat HTTP methods, status codes, request bodies, and response schemas as part of the API contract.
- Use failing tests as diagnostic evidence rather than treating every failure as an application defect.

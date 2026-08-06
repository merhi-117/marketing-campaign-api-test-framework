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

## 7. Current Project Status

### Completed

- Initial API contract
- Local Express server
- Health endpoint
- In-memory campaign storage
- Campaign creation
- Campaign validation
- Duplicate detection
- Campaign lookup by ID
- Manual positive and negative API testing
- Maven configuration
- First JUnit and REST Assured test
- Deliberate test failure and recovery
- Feature-branch workflow and GitHub push

### Not completed yet

- `GET /campaigns`
- `PATCH /campaigns/{id}/status`
- Subscriber endpoints
- `/test/reset`
- Automated negative campaign tests
- Automated campaign lookup tests
- Reusable REST Assured configuration
- Request and response model classes
- Cucumber/BDD scenarios
- GitHub Actions
- WireMock
- Accessibility and Salesforce Marketing Cloud study

---

## 8. Next Improvements

- Add `/test/reset` so tests begin with predictable data.
- Add REST Assured tests for invalid campaign requests.
- Add tests for duplicate campaign names.
- Add tests for campaign lookup, invalid IDs, and missing campaigns.
- Refactor repeated REST Assured setup into reusable configuration.
- Separate test data, API client methods, and assertions as the framework grows.
- Complete the remaining endpoint contracts.
- Add subscriber functionality and consent-related business rules.
- Add Cucumber scenarios after the core API tests are stable.
- Add GitHub Actions after the test suite runs reliably from the command line.


## Reset Campaigs Data

###what I learned

- BeforeEach is used before every @Test method
- JUnit does not guarantee that tests will always run in the order they appear in your file. So test should not depend on the execcution order.
- 
# Marketing Campaign API – Learning Notes

## What I built

- Created the initial API endpoint summary.
- Defined the operations needed for campaigns and subscribers.

## What I expected

- I expected the contract to identify the HTTP method, endpoint, and purpose.
- I expected that some details would still be missing.

## What happened

- The endpoint summary covered the main operations.
- I learned that an endpoint list is not a complete API contract.
- Installed Apache Maven 3.9.16.
- Configured MAVEN_HOME and added Maven's bin directory to PATH.
- Verified that Maven uses Java 21.

## What failed

- Maven was not recognized because it was not installed or added to PATH.
- The Markdown table divider was not correctly structured.
- The project and contract filenames contained spelling errors.
- Request bodies, responses, validation rules, and status codes were missing.

## Why it failed

- Maven is a separate build tool and is not included automatically with Java.
- I initially focused on endpoint names instead of the complete agreement between the API client and server.

## What I changed

- Corrected the project and file names.
- Corrected the endpoint table.
- Began defining the contract for POST /campaigns.
- corrected the POST/Campaign error response.
- Removed incorrect 404 not found response from campaign creation.
- Added request field definitions and validation rules.
- Changed error responses to structured JSON objects.

## What I learned

- Java and Maven are separate tools.
- Maven manages dependencies and runs Java builds and tests.
- An API contract includes requests, responses, validation rules, and status codes.
- Test data can be reset, but test cases are not reset.
- 404 response applies when a requested resource cannot be found, not when creating a new resource.
- A 409 response can represent a conflict with existing data.
- Validation rules become the basis for positive and negative automated tests. 

## What I would improve

- Define the complete contract for every endpoint.
- Add examples of successful and unsuccessful requests.
- Confirm that every business rule has at least one test scenario.
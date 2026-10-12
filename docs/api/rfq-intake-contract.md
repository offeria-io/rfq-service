
# RFQ Intake API Contract

## Scope

This document describes the current manual RFQ intake contract
implemented by `rfq-service`.

The contract establishes:

- Manual RFQ submission
- Registry reference validation
- Intake source tracking
- Intake review status metadata
- Compatibility with existing RFQ records

File upload, document extraction, automated RFQ intake, and
review transition endpoints are outside the scope of the
current implementation.

## Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/rfqs` | Create a manual RFQ |
| GET | `/api/v1/rfqs` | List RFQs |
| GET | `/api/v1/rfqs/{id}` | Retrieve an RFQ by UUID |

## Manual RFQ Creation

### Request

**Endpoint:** `POST /api/v1/rfqs`

**Content-Type:** `application/json`

Example:

```json
{
  "rfqNumber": "RFQ-2026-001",
  "title": "Supply of Industrial Equipment",
  "clientId": "11111111-1111-1111-1111-111111111111",
  "projectId": "22222222-2222-2222-2222-222222222222",
  "contractId": null,
  "workLocationId": null,
  "attentionContactId": null
}
```

The UUIDs above are illustrative and must reference valid records.

### Required Fields

- `rfqNumber`: Nonblank string
- `title`: Nonblank string
- `clientId`: Non-null UUID
- `projectId`: Non-null UUID

### Optional Fields

- `contractId`: UUID
- `workLocationId`: UUID
- `attentionContactId`: UUID

The service validates registry references and their applicable
business relationships.

### Successful Response

**HTTP 201 Created**

The response includes:

- `id`
- `rfqNumber`
- `offerNumber`
- `title`
- `clientId`, `clientCode`, `clientName`
- `projectId`, `projectCode`, `projectName`
- `contractId`, `contractCode`
- `workLocationId`, `workLocationCode`
- `attentionContactId`, `attentionContactName`
- `folderName`
- `status`
- `intakeSource`
- `intakeStatus`
- `createdAt`
- `updatedAt`

New manually created RFQs start with:

| Field | Initial Value |
|---|---|
| `intakeSource` | `MANUAL` |
| `intakeStatus` | `PENDING_REVIEW` |
| `status` | `PENDING` |

Offer numbering and folder naming remain managed by the
existing P03 business logic.

## Validation and Error Handling

The controller uses Jakarta Bean Validation through `@Valid`.

### Required Field Validation

The following conditions result in **HTTP 400 Bad Request**:

- Missing or blank `rfqNumber`
- Missing or blank `title`
- Missing `clientId`
- Missing `projectId`
- Invalid UUID format

Bean Validation errors return a JSON object containing
field names and their validation messages.

Example:

```json
{
  "rfqNumber": "RFQ number is required",
  "title": "Title is required",
  "clientId": "Client ID is required",
  "projectId": "Project ID is required"
}
```

### Business Validation

The service validates:

- Client references
- Project references
- Client/Project relationships
- Optional registry references and their relationships
- Duplicate RFQ numbers

Business validation failures handled as runtime exceptions
currently return HTTP 400 with an error response containing:

- `timestamp`
- `message`
- `status`

### HTTP Validation Tests

`RfqControllerTest` verifies:

- Empty request rejection
- Blank required field rejection
- Invalid UUID rejection
- No service invocation when request validation fails

These tests use MockMvc and Mockito.

## Intake Source

The `intakeSource` field identifies how an RFQ entered
the system.

Supported values:

| Value | Description |
|---|---|
| `MANUAL` | RFQ created through manual submission |
| `FILE` | Reserved for future document-based intake |

The current POST endpoint creates `MANUAL` RFQs only.

The client does not submit `intakeSource` in `RfqRequest`.

File upload and automated extraction are not yet implemented.

## Intake Review Status

The `intakeStatus` field tracks the review state of
the received RFQ information.

It is independent of the commercial RFQ `status`.

Supported values:

| Value | Description |
|---|---|
| `PENDING_REVIEW` | Awaiting intake review |
| `VALIDATED` | Intake information reviewed and approved |
| `REJECTED` | Intake information rejected during review |

### Initial State

Every newly created manual RFQ starts with:

```text
intakeSource = MANUAL
intakeStatus = PENDING_REVIEW
status = PENDING
```

### Separation of Responsibilities

`intakeStatus` represents the intake review lifecycle.

`status` represents the commercial RFQ lifecycle.

The commercial lifecycle will be developed separately
under P05.

### Current Limitations

The current API does not provide endpoints to:

- Approve intake records
- Reject intake records
- Change intake review status
- Record review decisions or reviewer details

The `VALIDATED` and `REJECTED` values are defined for
future review workflows.

## Database Migrations

Database schema changes are managed through Flyway.

### Migration V8 — Intake Source

**File:** `V8__add_rfq_intake_source.sql`

Adds:

- `rfqs.intake_source VARCHAR(20) NOT NULL`
- Default value `MANUAL`
- CHECK constraint allowing `MANUAL` and `FILE`

Existing RFQs receive `MANUAL` as a compatibility backfill.

This default does not establish their historical intake origin.

### Migration V9 — Intake Review Status

**File:** `V9__add_rfq_intake_status.sql`

Adds:

- `rfqs.intake_status VARCHAR(30) NOT NULL`
- Default value `PENDING_REVIEW`
- CHECK constraint allowing:
    - `PENDING_REVIEW`
    - `VALIDATED`
    - `REJECTED`

Existing RFQs receive `PENDING_REVIEW` as a compatibility backfill.

This does not mean their historical review state is known.

The commercial RFQ `status` remains unchanged.

### Migration Execution

Flyway applies pending migrations during application startup.

The migrations have been exercised using the H2
PostgreSQL compatibility mode in the test environment.

Execution against a real PostgreSQL instance has not
yet been independently verified.

## Legacy Compatibility

Existing RFQs retain their:

- Identifiers
- RFQ numbers
- Offer numbers
- Folder names
- Commercial statuses
- Registry references

The response mapper reads the persisted `clientName` field,
allowing legacy RFQs without a Client relationship to
remain readable.

New intake metadata does not alter existing offer numbering
or folder naming rules.

## Testing

The implementation includes:

- Service tests for initial intake metadata
- Mapper tests for intake metadata in responses
- Legacy client name compatibility tests
- HTTP request validation tests
- Spring Boot application context test with Flyway enabled

Latest complete local test result:

```text
Tests run: 60
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

## Future Work

Later P04 work will implement:

- Intake review status transitions
- Review decisions and related metadata
- File ingestion
- Document parsing and extraction
- Validation of extracted information
- Manual review of extracted data

RFQ item management and the commercial RFQ lifecycle
belong to P05.

The current contract provides the foundation for these
future capabilities without implementing them prematurely.

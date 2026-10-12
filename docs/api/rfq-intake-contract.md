# RFQ Intake API Contract

## Scope

This document describes the current manual RFQ intake contract
implemented by rfq-service.

File upload, document extraction, and automated RFQ intake are
outside the scope of the current API.

## Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/v1/rfqs | Create a manual RFQ |
| GET | /api/v1/rfqs | List RFQs |
| GET | /api/v1/rfqs/{id} | Retrieve an RFQ by UUID |

## Manual RFQ Creation

### Request

POST /api/v1/rfqs

Content-Type: application/json

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
The UUIDs above are illustrative and must reference valid records.
Required Fields
- rfqNumber: nonblank string
- title: nonblank string
- clientId: non-null UUID
- projectId: non-null UUID
Optional Fields
- contractId
- workLocationId
- attentionContactId
The service validates registry references and their applicable
relationships.
Successful Response
HTTP 201 Created
The response includes:
- id
- rfqNumber
- offerNumber
- title
- clientId, clientCode, clientName
- projectId, projectCode, projectName
- contractId, contractCode
- workLocationId, workLocationCode
- attentionContactId, attentionContactName
- folderName
- status
- intakeSource
- createdAt
- updatedAt
For manual creation, intakeSource is MANUAL and the initial
RFQ status is PENDING.
Offer numbering and folder naming remain managed by the
existing P03 business logic.
Validation
The controller uses Jakarta Bean Validation through @Valid.
Blank rfqNumber/title and missing clientId/projectId violate
the request contract.
Invalid registry references and inconsistent relationships
are rejected by the service.
Exact HTTP error statuses and response-body formats are
defined by the application's exception handling and require
separate API-level verification.
Intake Source
Supported database values:
- MANUAL
- FILE
The current POST endpoint creates MANUAL RFQs only.
FILE is reserved for a future ingestion workflow. The current
API does not provide file upload or extraction.
The client does not submit intakeSource in RfqRequest.
Migration V8
Migration:
V8__add_rfq_intake_source.sql
Adds:
- rfqs.intake_source VARCHAR(20) NOT NULL
- Default value MANUAL
- Database CHECK constraint allowing MANUAL and FILE
Existing RFQs receive MANUAL as a compatibility backfill.
This default does not establish their historical origin.
Flyway applies the migration during application startup.
Legacy Compatibility
Existing RFQs retain their identifiers, status, registry
references, offer numbers, and folder names.
The response mapper reads the persisted clientName field,
allowing legacy RFQs without a client relation to remain
readable.
Future Work
Later P04 work will define additional intake/review lifecycle
metadata, file ingestion, extraction, and review operations.
Those capabilities are not implemented by this contract.

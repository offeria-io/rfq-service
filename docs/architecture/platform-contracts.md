# Offeria Platform Contracts

**Status:** Platform baseline  
**Updated:** 2026-10-03

This document defines conventions all Offeria services should converge on. It records the current repository baseline separately from the target standard.

## Current version matrix

| Repository | Spring Boot | Java | Spring Cloud | Spring AI |
|---|---:|---:|---:|---:|
| api-gateway | 3.5.11 | 21 | 2024.0.0 | — |
| config-server | 3.5.11 | 21 | 2025.0.2 | — |
| discovery-service | 3.5.11 | 21 | 2025.0.2 | — |
| auth-service | 3.5.11 | 21 | — | — |
| rfq-service | 3.5.11 | 21 | 2025.0.2 | — |
| material-service | 3.5.11 | 21 | 2025.0.2 | 1.0.9 |
| proposal-service | 3.4.2 | 21 | 2024.0.0 | — |
| document-service | 3.4.2 | 21 | 2024.0.0 | — |
| file-service | 3.4.2 | 21 | 2024.0.0 | — |
| audit-service | 3.4.2 | 21 | 2024.0.0 | — |
| search-service | 3.4.2 | 21 | 2024.0.0 | — |
| translation-service | 3.4.2 | 21 | 2024.0.0 | — |
| notification-service | 3.5.11 | 21 | 2025.0.2 | — |
| frontend | 3.4.2 | 21 | — | — |
| offeria-mcp-server | 3.5.11 | 21 | — | 1.0.9 |

The matrix shows version drift. Alignment is implementation work, not a documentation-only change.

## Target baseline

- Java 21.
- Spring Boot 3.5.x.
- A tested Spring Cloud 2025.x release compatible with the selected Spring Boot version.
- Maven builds.
- PostgreSQL for relational persistence.
- Flyway for controlled relational schema migrations.
- Kafka for asynchronous business facts, not ordinary CRUD.
- MinIO for binary object storage.
- Elasticsearch for rebuildable search projections.
- Docker packaging and Kubernetes deployment where applicable.

## REST API contract

New APIs use `/api/v1/...`.

Every externally consumed service API must provide:

- request validation;
- stable request/response DTOs;
- consistent error responses;
- correct HTTP status codes;
- pagination for unbounded collections;
- authorization on sensitive operations;
- correlation ID propagation;
- OpenAPI documentation.

Persistence entities are not public API contracts.

### Standard error envelope

```json
{
  "timestamp": "2026-10-03T09:00:00Z",
  "status": 400,
  "code": "RFQ_VALIDATION_FAILED",
  "message": "RFQ validation failed",
  "correlationId": "uuid",
  "fieldErrors": []
}
```

## OpenAPI expectations

For services exposing REST APIs:

1. OpenAPI must describe public endpoints, request/response schemas, validation constraints and relevant status codes.
2. Security schemes must be documented for protected endpoints.
3. API version changes that break consumers require an explicit versioning decision.
4. Generated documentation must match runtime behavior.
5. Contract changes must be reviewed together with dependent services.

## Kafka contract

Events represent completed facts. Recommended naming:

`<domain>.<fact>.v<major>`

Examples: `rfq.created.v1`, `proposal.approved.v1`, `document.generated.v1`.

Common envelope fields:

- eventId
- eventType
- occurredAt
- correlationId
- causationId
- actorId
- aggregateType
- aggregateId
- version
- payload

Consumers must tolerate duplicate delivery. Atomic state/event publication should use a transactional outbox or equivalent reliable pattern when required.

## Persistence contract

- A service owns its schema/database.
- No cross-service database foreign keys.
- Cross-service relationships use stable IDs.
- Production schema changes use migrations.
- Production Hibernate schema mode is `validate`.
- `update`, `create` and `create-drop` are not production migration strategies.

## Security/configuration contract

- No production secret is committed to source control.
- Secrets are supplied by environment/deployment secret management.
- auth-service owns identity and token issuance.
- api-gateway is the external routing boundary.
- Sensitive domain actions remain authorized inside the owning service.
- Default/development credentials must never be promoted unchanged to production.

## Observability contract

HTTP, Kafka, logs and audit records propagate a correlation ID. Production readiness should include health/readiness endpoints, structured logs, metrics and traceability across distributed operations.

## Change policy

Version alignment, gateway cleanup, migration conversion and secret externalization are tracked as implementation work. They must be changed through reviewed PRs with tests rather than silently rewritten as part of documentation.

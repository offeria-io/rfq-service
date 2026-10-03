# Offeria Platform Architecture

**Status:** Architecture Baseline  
**Issue:** #1 — Offeria Platform Baseline & RFQ Architecture  
**Last Updated:** 2026-10-03

---

## 1. Purpose

Offeria is an RFQ-to-delivery platform designed to manage the complete commercial workflow from receiving a customer RFQ through material resolution, supplier sourcing, pricing, proposal generation, submission, award tracking, and delivery documentation.

This document is the authoritative architectural baseline for the Offeria platform.

The target business flow is:

RFQ IN
→ Ingestion & Extraction
→ Human Review
→ Client / Project / Contract Resolution
→ RFQ Items
→ Material Knowledge Resolution
→ Supplier Quotations
→ Pricing
→ Technical Proposal
→ Commercial Proposal
→ Approval
→ RFQ OUT / Submission Package
→ Submission
→ Award / Loss
→ Delivery
→ Archive / Audit / Search

---

## 2. Architecture Principles

Offeria follows these platform-wide principles:

1. Each business domain has one authoritative owner.
2. Services never access another service's database directly.
3. REST is used for synchronous commands and queries.
4. Kafka is used for asynchronous business events.
5. Events represent facts that already happened.
6. Approved database knowledge takes precedence over AI-generated content.
7. AI-generated authoritative data requires human approval.
8. Generated documents use versioned templates.
9. Binary files are managed through `file-service`.
10. Important business state changes are auditable.
11. Cross-service references use stable IDs rather than duplicated mutable entities.
12. Production database schemas are controlled through migrations.
13. Secrets must be supplied externally and never embedded in production configuration.
14. APIs and events must evolve through explicit versioning.
15. Business operations must be designed for retry and idempotency where distributed processing is involved.

---

## 3. Service Ownership

### api-gateway

Responsibilities:

- External API entry point
- Request routing
- Authentication token validation
- Cross-cutting request policies
- Correlation ID propagation
- Rate limiting where required

Must not own:

- Users
- RFQs
- Materials
- Pricing
- Proposals
- Business persistence

Authentication/business identity belongs to `auth-service`.

---

### discovery-service

Responsibilities:

- Eureka service discovery for environments where discovery is used
- Service registration and lookup

It contains no business domain.

---

### config-server

Responsibilities:

- Centralized application configuration
- Environment-specific service configuration

Secrets must be injected from the deployment environment or secret management system.

---

### auth-service

Authoritative owner of:

- Users
- Credentials
- Roles
- Authentication
- Authorization identity
- JWT issuance

It must provide identity information used by downstream services without duplicating user credentials elsewhere.

---

### rfq-service

Authoritative owner of:

- RFQ
- RFQ Item
- RFQ lifecycle
- Customer RFQ reference
- Offer number
- RFQ revisions
- Deadlines
- RFQ ingestion state
- Material resolution reference for each RFQ item
- Submission status

It orchestrates the RFQ business lifecycle but does not own material knowledge, prices, documents, or files.

---

### material-service

Authoritative owner of:

- Canonical Material
- Material Alias
- Iraqi-market terminology
- Material normalization
- Material matching
- Material translation knowledge
- Legacy material knowledge import
- Material AI suggestions
- Material approval workflow
- Semantic material discovery

Approved Material Knowledge Base data always has priority over AI-generated suggestions.

---

### translation-service

Responsible for:

- General-purpose text translation outside canonical material terminology
- Reusable non-material translation workflows

It must not become authoritative for Material Knowledge Base terminology.

Material-specific Iraqi/Arabic terminology belongs to `material-service`.

---

### supplier/pricing domain

Responsible for:

- Supplier registry
- Supplier contacts
- Supplier material capabilities
- Supplier quotations
- Supplier quotation items
- Price history
- Pricing calculations
- Currency snapshots
- Freight/cost inputs
- Markup and margin
- Commercial pricing snapshots

The final deployment boundary may initially combine supplier and pricing functionality if this reduces unnecessary microservice fragmentation.

The domain boundary must remain explicit even if implemented in one service.

---

### proposal-service

Authoritative owner of:

- Technical Proposal
- Commercial Proposal
- Proposal Items
- Proposal revisions
- Proposal lifecycle
- Technical compliance/deviations
- Commercial terms
- Proposal approval state

Pricing values used by an approved proposal must reference an immutable pricing snapshot.

---

### document-service

Responsible for:

- Document templates
- Template versions
- DOCX generation
- Excel generation
- PDF generation
- Technical Proposal rendering
- Commercial Proposal rendering
- RFQ OUT rendering
- Delivery document rendering

It does not own RFQ or proposal business state.

---

### file-service

Authoritative owner of binary storage metadata.

Responsibilities:

- Upload
- Download
- MinIO object storage
- File metadata
- Secure file access
- Generated document storage
- Attachment storage

Business services store only stable file references.

---

### delivery domain

Authoritative owner of:

- Delivery
- Delivery Item
- Partial delivery tracking
- Delivered quantity
- Remaining quantity
- Delivery lifecycle
- Delivery references
- Signed delivery documentation references

Delivery becomes active only for awarded business.

---

### audit-service

Authoritative owner of immutable audit records.

Records important changes including:

- RFQ lifecycle transitions
- Material approvals
- Price overrides
- Pricing approvals
- Proposal approvals
- Proposal revisions
- Submission
- Delivery transitions

---

### notification-service

Responsible for user-facing notifications triggered by business events.

Examples:

- Review required
- Approval required
- RFQ deadline approaching
- Document generation completed
- Document generation failed
- Proposal approved
- Submission completed

Notification delivery must not control the business transaction itself.

---

### search-service

Responsible for cross-domain search indexes.

Searchable domains include:

- RFQs
- Materials
- Clients
- Contracts
- Proposals

Search indexes are projections.

They are never the authoritative source of business data.

---

### offeria-mcp-server

Responsible for exposing approved Offeria capabilities as MCP tools.

Current Material tools include:

- `search_material`
- `get_material`
- `find_similar_material`
- `get_material_translation`
- `suggest_material_translation`

The MCP server:

- does not own business data;
- does not access service databases directly;
- calls authoritative Offeria APIs;
- must respect the same authorization and approval boundaries as other clients.

---

### frontend

Responsible for:

- User interface
- Workflow presentation
- Validation feedback
- Review screens
- Approval screens
- Document/package actions
- Search
- Delivery management

The frontend never becomes an authoritative business data source.

---

## 4. RFQ Lifecycle

The canonical RFQ lifecycle is:

```text
RECEIVED
   |
   v
INGESTING
   |
   v
REVIEW_REQUIRED
   |
   v
DRAFT
   |
   v
MATERIAL_RESOLUTION
   |
   v
SOURCING
   |
   v
PRICING
   |
   v
PROPOSAL_PREPARATION
   |
   v
APPROVAL_REQUIRED
   |
   +------> REJECTED_FOR_CORRECTION
   |                 |
   |                 +----> PROPOSAL_PREPARATION
   |
   v
READY_TO_SUBMIT
   |
   v
SUBMITTED
   |
   +------> LOST
   |
   +------> CANCELLED
   |
   v
AWARDED
   |
   v
DELIVERY_IN_PROGRESS
   |
   v
COMPLETED
```

Not every RFQ must pass through automated ingestion.

Manually created RFQs may enter at `DRAFT` after mandatory validation.

---

## 5. RFQ Revision Model

RFQs and proposals must preserve business history.

Changes after submission must not silently overwrite submitted data.

Typical proposal revisions:

```text
REV-00
REV-01
REV-02
...
```

A submitted or approved revision becomes immutable.

A modification creates a new revision derived from the previous one.

---

## 6. Core Cross-Service Identifiers

Cross-service relationships use IDs rather than foreign keys across databases.

Canonical identifiers include:

```text
userId
clientId
contactId
projectId
contractId

rfqId
rfqItemId
rfqRevisionId

materialId
materialAliasId

supplierId
supplierQuotationId
supplierQuotationItemId

pricingSnapshotId

proposalId
proposalRevisionId
proposalItemId

documentId
documentTemplateId
fileId

deliveryId
deliveryItemId
```

Services may store these IDs as references but must not create database foreign keys into another service's database.

---

## 7. Offer Number

Offeria requires a concurrency-safe Offer Number generator.

Offer numbers must never depend on:

```text
repository.count() + 1
```

because concurrent RFQ creation can generate duplicate numbers.

The authoritative implementation must use a database-backed sequence or another atomic allocation strategy.

Example logical representation:

```text
Offer#3198
```

The numeric sequence is immutable once assigned.

---

## 8. RFQ Folder Naming

The business folder convention is:

```text
Offer#<NUMBER> <PROJECT-CODE> <RFQ-NO> <TITLE>
```

Example:

```text
Offer#3198 BSR-RML-Rumaila-GPP CPECC-DIV1-YJRML-2026-0078 MQ-IQ04062 Civil engineering consumables
```

The logical folder/package name belongs to the RFQ business domain.

Physical object storage paths remain an implementation concern of `file-service`.

---

## 9. REST Communication

REST is used when the caller requires an immediate response.

Examples:

```text
frontend → api-gateway
api-gateway → auth-service

rfq-service → material-service
rfq-service → file-service

proposal-service → rfq-service
proposal-service → pricing domain

document-service → file-service

offeria-mcp-server → material-service
```

Typical REST operations:

- CRUD
- validation
- lookup
- search requiring immediate response
- material resolution
- pricing queries
- approval commands
- file operations

---

## 10. Kafka Communication

Kafka is used for asynchronous business facts and decoupled processing.

Recommended event examples:

```text
rfq.received.v1
rfq.created.v1
rfq.reviewed.v1
rfq.material-resolution-completed.v1
rfq.ready-for-pricing.v1
rfq.submitted.v1
rfq.awarded.v1
rfq.lost.v1

material.approved.v1
material.alias-approved.v1

supplier-quotation.received.v1

pricing.approved.v1

proposal.created.v1
proposal.approved.v1
proposal.submitted.v1

document.generation-requested.v1
document.generated.v1
document.generation-failed.v1

delivery.created.v1
delivery.completed.v1

audit.event.v1
notification.requested.v1
```

Kafka must not replace simple synchronous CRUD APIs.

---

## 11. Event Envelope

Platform business events should use a common envelope.

Example:

```json
{
  "eventId": "uuid",
  "eventType": "rfq.created.v1",
  "occurredAt": "2026-10-03T09:00:00Z",
  "correlationId": "uuid",
  "causationId": "uuid",
  "actorId": "uuid",
  "aggregateType": "RFQ",
  "aggregateId": "uuid",
  "version": 1,
  "payload": {}
}
```

Consumers must be designed to tolerate duplicate delivery.

Where business state and event publication must be atomic, the implementation should use a transactional outbox or equivalent reliable publication mechanism.

---

## 12. Correlation and Trace IDs

Every incoming business request receives a:

```text
X-Correlation-Id
```

If the caller supplies one, it is propagated.

Otherwise the gateway creates one.

The correlation ID must propagate through:

```text
HTTP
Kafka events
logs
audit records
document-generation requests
```

This allows one RFQ operation to be traced across services.

---

## 13. Database Ownership

Every persistent service owns its own schema/database.

Forbidden:

```text
rfq-service → material database
proposal-service → RFQ database
document-service → proposal database
```

Required:

```text
service → REST API
```

or asynchronous synchronization through:

```text
Kafka event → local projection
```

when appropriate.

---

## 14. Database Migration Policy

Production-capable persistent services must use controlled migrations.

Target standard:

```text
Flyway
```

Hibernate schema behavior:

```text
production:
ddl-auto: validate
```

Schema creation through:

```text
ddl-auto: update
ddl-auto: create
ddl-auto: create-drop
```

must not be the production schema-management strategy.

`material-service` provides the current reference implementation for this migration approach.

---

## 15. Platform Technology Baseline

Target baseline:

```text
Java: 21
Spring Boot: 3.5.x
Spring Cloud: compatible 2025.x release train
PostgreSQL: primary relational database
Kafka: asynchronous business events
MinIO: object/file storage
Elasticsearch: global search projections
Flyway: database migrations
Docker: container packaging
Kubernetes: deployment orchestration
Maven: build system
```

Exact Spring Boot and Spring Cloud versions must be selected as a tested compatible pair and then standardized across services.

Upgrades must be performed through individual reviewed implementation issues rather than uncontrolled repository-wide edits.

---

## 16. API Standards

New service APIs use:

```text
/api/v1/...
```

Every API must provide:

- input validation;
- consistent error responses;
- correct HTTP status codes;
- pagination for large collections;
- stable DTOs;
- API documentation;
- authorization where required.

Business entities must not be exposed directly as persistence models when doing so couples the external contract to the database model.

---

## 17. Error Model

Target API error structure:

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

Service-specific error codes remain explicit while the outer response structure stays consistent.

---

## 18. Security Baseline

Production credentials and secrets must never be hard-coded.

Examples:

```text
JWT_SECRET
DATABASE_PASSWORD
CONFIG_SERVER_PASSWORD
MINIO_SECRET_KEY
AI_PROVIDER_API_KEY
```

They must be injected through environment/deployment secrets.

Authentication is owned by `auth-service`.

The gateway validates incoming authentication.

Business services still enforce authorization for sensitive domain actions.

Examples:

- pricing approval;
- proposal approval;
- material approval;
- manual price override;
- delivery completion.

---

## 19. AI Boundary

AI is an assistant, not the authoritative business database.

AI may assist with:

- RFQ extraction;
- material matching;
- unknown material translation;
- Iraqi terminology suggestions;
- duplicate detection;
- semantic search;
- specification extraction;
- classification.

AI must not:

- overwrite approved material knowledge;
- directly modify authoritative databases without validation;
- approve its own suggestions;
- determine final commercial pricing;
- bypass business authorization;
- silently replace human-approved content.

The priority order is:

```text
Approved Knowledge
        ↓
Deterministic Matching
        ↓
Historical Approved Data
        ↓
AI Suggestion
        ↓
Human Review
        ↓
Approved Knowledge
```

---

## 20. Document Architecture

Business services own document data.

`document-service` owns rendering.

Example:

```text
proposal-service
      |
      | approved proposal snapshot
      v
document-service
      |
      | template + data
      v
DOCX / XLSX / PDF
      |
      v
file-service
      |
      v
MinIO
```

Generated documents record:

- source entity;
- source revision;
- template version;
- generation timestamp;
- checksum;
- file reference.

Regeneration must not silently replace an approved historical document.

---

## 21. Search Architecture

Authoritative services publish business changes.

`search-service` maintains searchable projections.

Example:

```text
rfq-service
material-service
proposal-service
        |
        v
      Kafka
        |
        v
 search-service
        |
        v
 Elasticsearch
```

Search failure must never prevent authoritative business transactions from completing.

Indexes must be rebuildable from authoritative sources/events.

---

## 22. Audit Architecture

Important business changes produce audit information containing:

```text
actor
action
entityType
entityId
before
after
timestamp
correlationId
```

Audit records are immutable.

Audit infrastructure must not become the primary business database.

---

## 23. Failure and Retry Principles

Distributed operations must assume temporary failure.

Examples:

- material-service unavailable;
- Kafka temporarily unavailable;
- MinIO unavailable;
- document generation failure;
- notification failure.

Rules:

1. Do not silently lose work.
2. Do not create duplicates on retry.
3. Preserve failure state.
4. Retry asynchronous operations where safe.
5. Use idempotency keys/event IDs where required.
6. Use DLQ/recovery mechanisms for Kafka consumers where appropriate.
7. Surface business-blocking failures for human review.

---

## 24. End-to-End Ownership Flow

```text
Customer RFQ
     |
     v
file-service
     |
     v
rfq-service
     |
     +----> material-service
     |
     +----> supplier/pricing domain
     |
     v
proposal-service
     |
     v
approval
     |
     v
document-service
     |
     v
file-service
     |
     v
Submission
     |
     +----> LOST / CANCELLED
     |
     v
AWARDED
     |
     v
delivery domain
     |
     v
Delivery Forms
```

Cross-cutting services:

```text
auth-service
audit-service
notification-service
search-service
config-server
discovery-service
api-gateway
```

AI/MCP integration:

```text
AI capabilities
      |
      v
authoritative service APIs
      ^
      |
offeria-mcp-server
```

---

## 25. Current Architecture Gaps

The architecture audit identified platform gaps that must be addressed through separate implementation issues.

### API Gateway

Current routes still include prototype/example domains such as:

```text
user-service
product-service
```

Routes must be replaced with actual Offeria services.

The gateway currently contains business/auth persistence concerns that must be removed or relocated to `auth-service`.

### Version Alignment

Services currently use different Spring Boot / Spring Cloud baselines.

These must be standardized through controlled upgrades.

### Database Migrations

Several services still depend on Hibernate automatic schema changes.

Persistent services must move to controlled migrations.

### Security Configuration

Development/default credentials exist in several configurations.

Production secrets must be externalized.

### RFQ Domain

The existing RFQ model is still too small for the target workflow.

It requires:

- RFQ items;
- client/project/contract references;
- deadlines;
- revisions;
- material resolution;
- ingestion state;
- lifecycle rules;
- concurrency-safe offer numbering.

### Proposal Domain

The existing proposal model is a prototype and requires structured:

- proposal items;
- RFQ linkage;
- revisions;
- technical fields;
- commercial fields;
- pricing snapshots;
- approvals.

### Document Generation

Current document generators are prototypes.

Production implementation requires versioned company templates and proper Technical Proposal, Commercial Proposal, RFQ OUT and Delivery layouts.

### Notification Service

The service currently requires implementation of actual notification workflows.

### Frontend

Prototype/mock data must be replaced by real backend integration.

---

## 26. Delivery Strategy

Architecture changes will not be implemented as one large cross-repository change.

Work proceeds through independently reviewable packages:

```text
P01 Architecture & Platform Contracts
P02 Security, Gateway & Access Control
P03 Client, Project, Contract & Offer Registry
P04 RFQ Intake, Extraction & Validation
P05 RFQ Lifecycle & Item Management
P06 RFQ → Material Integration
P07 Supplier Registry & RFQ OUT
P08 Pricing & Commercial Calculation
P09 Technical Proposal
P10 Commercial Proposal & Approval
P11 Document Templates & Submission Package
P12 Award & Delivery
P13 Files, Archive & Search
P14 Audit & Notifications
P15 AI & MCP Expansion
P16 Production Readiness, CI/CD & E2E
```

Each package must be decomposed into GitHub issues before implementation.

---

## 27. Definition of Platform Completion

Offeria v1.0 is complete only when the following real workflow succeeds:

```text
Receive RFQ
→ import/extract RFQ
→ review RFQ
→ resolve materials
→ request supplier pricing
→ select/approve pricing
→ create technical proposal
→ create commercial proposal
→ approve proposal
→ generate final documents
→ create submission package
→ submit
→ record award
→ perform partial/full delivery
→ generate delivery forms
→ close and archive
```

The workflow must be covered by integration/end-to-end testing and must not depend on mock frontend data.

---

## 28. Architecture Decision

This document establishes the baseline architecture for subsequent Offeria development.

Future implementation may refine individual components, but changes to service ownership, authoritative data boundaries, or the RFQ-to-delivery lifecycle require an explicit architecture decision and review.

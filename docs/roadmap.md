# Offeria Delivery Roadmap

**Goal:** production-capable RFQ IN → proposal → submission → award/loss → delivery workflow.

Status reflects the platform plan, not marketing claims.

| Package | Scope | Status |
|---|---|---|
| P01 | Architecture & Platform Contracts | Done |
| P02 | Security, Gateway & Access Control | Done — baseline security complete; service-specific boundaries continue in owning packages |
| P03 | Client, Project, Contract & Offer Registry | Backlog |
| P04 | RFQ Intake, Extraction & Validation | Backlog |
| P05 | RFQ Lifecycle & Item Management | Backlog |
| P06 | RFQ → Material Integration | Backlog |
| P07 | Supplier Registry & RFQ OUT | Backlog |
| P08 | Pricing & Commercial Calculation | Backlog |
| P09 | Technical Proposal | Backlog |
| P10 | Commercial Proposal & Approval | Backlog |
| P11 | Document Templates & Submission Package | Backlog |
| P12 | Award & Delivery | Backlog |
| P13 | Files, Archive & Search | Backlog |
| P14 | Audit & Notifications | Backlog |
| P15 | AI & MCP Expansion | Backlog |
| P16 | Production Readiness, CI/CD & E2E | Backlog |

## Completed foundation

Material Knowledge Base work established canonical materials, aliases, normalization, legacy Excel ingestion/review/promotion, duplicate/similar detection, search, Iraqi terminology/translation APIs, AI suggestion approval, semantic discovery and MCP-facing APIs.

The dedicated MCP server exposes approved Material capabilities without direct database access and has Docker/Kubernetes readiness work.

## Known platform gaps

- Spring Boot/Spring Cloud version drift.
- Service-specific authorization boundaries remain to be completed as their owning services are implemented.
- Automatic Hibernate schema mutation in several services.
- Development/default credentials that must not become production secrets.
- RFQ domain lacks the full target lifecycle and item model.
- Offer-number allocation requires an atomic strategy.
- Supplier quotation/pricing workflow is not complete.
- Proposal domain is still prototype-level.
- Production document templates and submission packages are not complete.
- Delivery workflow is not complete.
- Notification workflow is not complete.
- Frontend still requires real end-to-end backend integration.
- Platform-wide CI/CD, observability and E2E verification remain P16 work.

## Completion criterion

Offeria v1.0 requires a tested real workflow:

RFQ receive/import → review → material resolution → supplier pricing → pricing approval → technical proposal → commercial proposal → approval → document/package generation → submission → award/loss → partial/full delivery → delivery forms → archive.

A feature is not complete merely because a class, endpoint or repository exists.

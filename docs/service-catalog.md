# Offeria Service Catalog

**Updated:** 2026-10-03

This catalog distinguishes intended ownership from current maturity.

| Repository | Intended responsibility | Current status / notable gap |
|---|---|---|
| api-gateway | External routing, token validation, request policies, correlation | Requires route cleanup and removal of business/persistence concerns |
| config-server | Centralized environment configuration | Requires production-grade external configuration/secrets |
| discovery-service | Service discovery | Infrastructure foundation present |
| auth-service | Users, roles, authentication, JWT identity | Requires platform-wide authorization hardening |
| rfq-service | RFQ, items, lifecycle, revisions, deadlines, offer number | Core model is still below target workflow |
| material-service | Canonical material knowledge, aliases, normalization, translation knowledge, AI review, semantic discovery | Most mature business domain; Material KB/MCP phase completed |
| proposal-service | Technical/commercial proposals, revisions and approvals | Prototype requires RFQ linkage, items, pricing snapshots and approvals |
| document-service | Versioned templates and rendering | Current generation is prototype-level; production templates required |
| file-service | Binary storage and file metadata | MinIO foundation exists; business linking/package conventions require expansion |
| audit-service | Immutable business audit records | Foundation exists; platform event coverage requires expansion |
| search-service | Cross-domain search projections | Requires verified event-driven indexing and rebuild strategy |
| translation-service | General non-material translation | Boundary must remain separate from material terminology ownership |
| notification-service | User-facing business notifications | Requires real notification workflows |
| frontend | Workflow UI, review, approval, search and delivery screens | Mock/prototype integration must be replaced by real backend flows |
| offeria-mcp-server | MCP exposure of approved Offeria capabilities | Material MCP tools implemented; broader tools belong to later roadmap work |

## Planned domain boundaries

Supplier/pricing and delivery are required business domains. Their final repository/deployment boundaries should be introduced only when implementation begins and the boundary is justified; documentation must not pretend that absent services already exist.

## Ownership rule

One domain has one authoritative owner. Other services reference it through stable IDs and APIs/events; they do not share its database.

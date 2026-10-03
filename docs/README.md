# Offeria Documentation

This directory is the entry point for Offeria platform documentation.

## Start here

1. [Platform Architecture](architecture/offeria-platform-architecture.md) — authoritative RFQ-to-delivery architecture, ownership and lifecycle.
2. [Platform Contracts](architecture/platform-contracts.md) — technology baseline, API/OpenAPI, event, security and persistence conventions.
3. [Service Catalog](service-catalog.md) — repository responsibilities and current implementation maturity.
4. [Delivery Roadmap](roadmap.md) — P01–P16 implementation plan and known gaps.
5. [Development Guide](development-guide.md) — branch, commit, PR, testing and migration conventions.
6. [Deployment Guide](deployment-guide.md) — Docker/Kubernetes direction and production-readiness requirements.

## Documentation policy

- GitHub code and merged documentation are the technical source of truth.
- Documentation describes both **target architecture** and **current implementation**; they must not be confused.
- Current gaps stay visible until implementation and verification are complete.
- Architecture ownership changes require review.
- Each implementation PR updates affected documentation when behavior, API, event, configuration or deployment changes.

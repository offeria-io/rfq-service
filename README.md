# RFQ Service

**Offeria — a product by [Al‑Wahha Al‑Sehriya](https://github.com/Al-Wahha-Al-Sehriya).**

[Company website](https://wahasehriya.com/) · [Offeria repositories](https://github.com/offeria-io)

This repository contains the RFQ domain service and hosts the current platform-level architecture documentation for Offeria.

## Product goal

Offeria is being built around the complete commercial workflow:

**RFQ IN → extraction/review → material resolution → supplier quotations → pricing → technical proposal → commercial proposal → approval → submission → award/loss → delivery → archive.**

The repository is under active development. Documentation intentionally shows both the target architecture and current implementation gaps.

## Documentation

Start with [docs/README.md](docs/README.md).

Key references:

- [Platform Architecture](docs/architecture/offeria-platform-architecture.md)
- [Platform Contracts](docs/architecture/platform-contracts.md)
- [Service Catalog](docs/service-catalog.md)
- [Delivery Roadmap](docs/roadmap.md)
- [Development Guide](docs/development-guide.md)
- [Deployment Guide](docs/deployment-guide.md)

## Current RFQ responsibility

`rfq-service` is the intended authoritative owner of RFQs, RFQ items, lifecycle, revisions, deadlines, offer numbers, ingestion state, material-resolution references and submission state.

The existing implementation is not yet the complete target domain. Follow the roadmap and GitHub issues for implementation status.

## Engineering principles

- service-owned data;
- stable cross-service IDs;
- REST for synchronous commands/queries;
- Kafka for asynchronous business facts;
- controlled database migrations;
- externally supplied production secrets;
- versioned APIs/events/templates;
- human approval for authoritative AI-assisted knowledge;
- auditable business state changes.

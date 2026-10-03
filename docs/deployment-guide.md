# Offeria Deployment Guide

**Status:** target deployment guidance; platform-wide production readiness is not yet complete.

## Local development

Services are Maven/Spring applications built around Java 21. Infrastructure dependencies vary by service and include PostgreSQL, Kafka, MinIO and Elasticsearch.

Before running a service, inspect its current `application*.yml/yaml`, Maven dependencies and README because configuration is not yet fully standardized across all repositories.

## Containers

Production-capable service images should:

- use reproducible builds;
- run as a non-root user where practical;
- expose only required ports;
- provide health/readiness endpoints;
- receive configuration/secrets externally;
- avoid embedding credentials in the image;
- use a minimal runtime image.

The dedicated MCP server already provides a stronger container baseline that can inform later service hardening.

## Kubernetes

Kubernetes deployment is part of the target production architecture. Manifests should define:

- Deployment;
- ClusterIP Service when network exposure is required;
- readiness/liveness probes;
- resource requests/limits;
- ConfigMap/Secret references as appropriate;
- environment-specific image references.

Service-to-service URLs in Kubernetes should use service DNS rather than localhost.

## Secrets

Never commit production values for database passwords, JWT signing keys, MinIO credentials, AI provider keys or configuration-server credentials.

## Database migration

Deployments of persistent services must run/validate controlled Flyway migrations before the new application version serves incompatible traffic.

## Observability

Production readiness requires:

- Actuator health/readiness;
- structured logs;
- correlation IDs;
- metrics;
- distributed traceability where useful;
- alerting for critical workflow failures.

## CI/CD target

P16 should standardize:

1. compile/test;
2. integration tests;
3. container build;
4. security/dependency checks;
5. image publication;
6. environment deployment;
7. smoke/readiness verification;
8. rollback strategy.

## Current limitation

Do not interpret the presence of a Dockerfile or Kubernetes manifest in an individual repository as proof that the entire Offeria platform is production-ready. Production readiness is verified at platform level in P16.
